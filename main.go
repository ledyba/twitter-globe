package main

import (
	"bufio"
	"flag"
	"fmt"
	"log"
	"net/http"
	"net/url"
	"os"
	"time"

	"github.com/ChimeraCoder/anaconda"
	"github.com/tjgq/broadcast"
	"golang.org/x/net/websocket"
)

var broadcaster *broadcast.Broadcaster

func getCred() {
	key, cred, _ := anaconda.AuthorizationURL("")
	fmt.Printf("access: %v\n", key)
	fmt.Printf("Key: ")
	buf := bufio.NewReader(os.Stdin)
	line, _, _ := buf.ReadLine()
	cred, _, _ = anaconda.GetCredentials(cred, string(line))
	fmt.Printf("cred: %s\n", cred)
}

func onTweet(api *anaconda.TwitterApi, tw anaconda.Tweet) {
	if _, err := tw.Longitude(); err != nil {
		return
	}
	if _, err := tw.Latitude(); err != nil {
		return
	}
	lat, _ := tw.Latitude()
	lng, _ := tw.Longitude()
	p := Post{
		Usr:    tw.User.ScreenName,
		Msg:    tw.Text,
		Lat:    lat,
		Lng:    lng,
		Image:  tw.User.ProfileImageURL,
		Client: tw.Source,
	}
	json, err := p.toJSON()
	if err != nil {
		log.Printf("Error: %s", err)
		return
	}
	broadcaster.Send(json)
}

func websockHandler(ws *websocket.Conn) {
	listener := broadcaster.Listen()
	defer listener.Close()
	for {
		select {
		case msg := <-listener.Ch:
			ws.Write(msg.([]byte))
		case <-time.After(time.Minute):
		}
	}

}

func repeatStreaming() {
	tw := anaconda.NewTwitterApi(OAuthToken, OAuthSecret)
	defer tw.Close()
	values := url.Values{}
	values.Add("locations", "-180,-90,180,90")
	stream := tw.PublicStreamFilter(values)
	for {
		select {
		case tweetRaw, received := <-stream.C:
			if !received {
				stream.Stop()
				return
			}
			if tweet, ok := tweetRaw.(anaconda.Tweet); ok {
				onTweet(tw, tweet)
			}
		}
	}
}
func setUpTwitterStream() {
	anaconda.SetConsumerKey(ConsumerKey)
	anaconda.SetConsumerSecret(ConsumerSecret)
	for {
		repeatStreaming()
	}
}

var port = flag.Int("port", 8080, "")

func main() {
	flag.Parse() // Scan the arguments list
	broadcaster = broadcast.New(0)
	go setUpTwitterStream()
	http.Handle("/public", websocket.Handler(websockHandler))
	http.Handle("/", http.FileServer(assetFS()))

	log.Printf("Start at http://localhost:%d/", *port)
	log.Fatal(http.ListenAndServe(fmt.Sprintf(":%d", *port), nil))
}
