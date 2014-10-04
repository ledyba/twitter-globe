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
		new AtmosphereClient {
			val f : PublicStream.GeoListener =
				new PublicStream.GeoListener {
					override def onPosted(lng:Double, lat:Double, status:Status, usr:User) = {
						val v =
							("lng" -> lng) ~
							("lat" -> lat) ~
							("msg" -> status.getText()) ~
							("usr" -> usr.getScreenName()) ~
							("image" -> usr.getBiggerProfileImageURL())~
							("client" -> status.getSource());
						send(compact(render(v)));
					}
				};
			override def receive ={
				case Connected => {
					PublicStream.addGeoListener(f);
				}
				case Disconnected(disconnector, Some(error)) => {
					PublicStream.removeGeoListener(f);
				}
				case Error(Some(error)) => {
					PublicStream.removeGeoListener(f);
				}
				case TextMessage(text) => Unit
				case JsonMessage(json) => Unit
			}
		};
	}

}
