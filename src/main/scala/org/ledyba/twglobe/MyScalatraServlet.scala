package org.ledyba.twglobe

import org.scalatra._
import scalate.ScalateSupport
import twitter4j.TwitterFactory
import org.ledyba.twglobe.tw.TwHelper
import twitter4j.auth.RequestToken
import org.ledyba.twglobe.tw.{PublicStream, TsuraiStream}
import twitter4j.User
import org.scalatra.atmosphere._
import org.scalatra.json.{JValueResult, JacksonJsonSupport}
import org.json4s._
import JsonDSL._
import java.util.Date
import java.text.SimpleDateFormat
import org.fusesource.scalate.Template
import scala.concurrent._
import ExecutionContext.Implicits.global
import com.fasterxml.jackson.annotation.JsonFormat
import twitter4j.Status
import grizzled.slf4j.Logger


class StreamClient(render : JValue => OutboundMessage ) extends AtmosphereClient with PublicStream.GeoListener {
	@transient private[this] val lg = Logger[StreamClient]
	override def receive ={
		case Connected => {
			PublicStream.addGeoListener(this);
		}
		case Disconnected(disconnector, _) => {
			PublicStream.removeGeoListener(this);
		}
		case Error(_) => {
			PublicStream.removeGeoListener(this);
		}
		case TextMessage(text) => Unit
		case JsonMessage(json) => Unit
	}
	override def onPosted(lng:Double, lat:Double, status:Status, usr:User) = {
		val v:JValue =
			("lng" -> lng) ~
			("lat" -> lat) ~
			("msg" -> status.getText()) ~
			("usr" -> usr.getScreenName()) ~
			("image" -> usr.getBiggerProfileImageURL())~
			("client" -> status.getSource());
		val r = render(v);
		broadcast(r, Me);
	}
}

class MyScalatraServlet extends TwitterGlobeStack
	with JValueResult
	with JacksonJsonSupport with SessionSupport
	with AtmosphereSupport
{

	get("/") {
		contentType = "text/html"
		//jade("index");
		jade("/map", "layout" -> "")
	}

	protected implicit val jsonFormats: Formats = DefaultFormats
	sealed class Post(lng:Double, lat:Double, usr:User, msg:String);
	atmosphere("/public") {
		new StreamClient(x => compact(render(x)));
	}

}
