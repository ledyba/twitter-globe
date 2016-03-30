package main

import (
	"bufio"
	"flag"
	"fmt"
	"os"

	"github.com/ChimeraCoder/anaconda"
)

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

	setUpTwitterStream()

}
