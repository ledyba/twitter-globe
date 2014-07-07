package org.ledyba.twglobe.tw

import twitter4j.Twitter
import twitter4j.TwitterFactory
import javax.servlet.http.HttpServletRequest
import org.ledyba.twglobe.Util
import twitter4j.AsyncTwitterFactory
import twitter4j.TwitterStreamFactory
import twitter4j.conf.ConfigurationBuilder

object TwHelper {
	val conf = 
		(new ConfigurationBuilder())
		.setAsyncNumThreads(3)
		.setOAuthConsumerKey(Const.API_KEY)
		.setOAuthConsumerSecret(Const.API_SECRET)
		.build()
	def getTwitter() = {
		val tw = new TwitterFactory(conf).getInstance();
		tw
	}
	def getTwitterAsync() = {
		val tw = new AsyncTwitterFactory(conf).getInstance();
		tw
	}
	def getStream() = {
		val tw = new TwitterStreamFactory(conf).getInstance();
		tw
	}
	def getRequestToken(request : HttpServletRequest, redirect:String) = {
		getTwitter.getOAuthRequestToken(Util.getApplicationRoot(request)+redirect);
	}
}