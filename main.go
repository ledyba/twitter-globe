package main

import (
	"bufio"
	"flag"
	"fmt"
	"log"
	"net/http"
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

func setUpTwitterStream() {
	anaconda.SetConsumerKey(ConsumerKey)
	anaconda.SetConsumerSecret(ConsumerSecret)
	tw := anaconda.NewTwitterApi(OAuthToken, OAuthSecret)
	defer tw.Close()
	stream := tw.UserStream(nil)
	for {
		select {
		case tweetRaw, closed := <-stream.C:
			if closed {
				return
			}
			if tweet, ok := tweetRaw.(anaconda.Tweet); ok {
				onTweet(tw, tweet)
			}
		}
	}
}

func main() {
	flag.Parse() // Scan the arguments list
	broadcaster = broadcast.New(0)
	setUpTwitterStream()
	http.Handle("/public", websocket.Handler(websockHandler))
	http.Handle("/", http.FileServer(http.Dir("./")))
	if err := http.ListenAndServe(":8080", nil); err != nil {
		panic("ListenAndServe: " + err.Error())
	}
}
