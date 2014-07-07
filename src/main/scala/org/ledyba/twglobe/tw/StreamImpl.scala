package org.ledyba.twglobe.tw

import scala.collection.mutable.HashSet
import twitter4j.TwitterStream
import twitter4j.FilterQuery

object PublicStream extends Stream {
	override def onRun(st:TwitterStream)={
		println("Starting Public Stream...");
		st.sample();
	}
}

object TsuraiStream extends Stream {
	override def onRun(st:TwitterStream)={
		val pl = Array(
				Array(122.56, 20.25),
				Array(123.56, 45.35311)
				)
		
		st.filter(new FilterQuery().track(Array("つらい", "悲","涙","死","鬱")))
	}
}