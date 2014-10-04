package org.ledyba.twglobe.tw

import twitter4j.auth.AccessToken
import twitter4j.StatusAdapter
import twitter4j.Status
import scala.collection.mutable.HashSet
import twitter4j.User
import twitter4j.TwitterStream
import java.util.Timer
import java.util.TimerTask

abstract class Stream() {
	val adapter = new StatusAdapter(){
		override def onStatus(status : Status) = {
			Stream.this.onPosted(status);
		}
	}
	val timer = new Timer();
	var tw:TwitterStream = null;
	def refresh():Unit = {
		if(tw == null){
			timer.schedule(new TimerTask(){
				override def run():Unit = {
					refresh();
				}
			}, 1000*1200, 1000*1200);
		}else{
			tw.shutdown();
			tw.cleanUp();
			tw = null;
		}
		tw = TwHelper.getStream();
		tw.setOAuthAccessToken(new AccessToken("7611042-2RaZcpiBp1ytiSL99HvOTKiZn0Ee88E7ol7Ey8uupv", "eFQZTy3kAltUfBz2hx4evu7GgnzkbyYv3QPFAZQYjfRhZ"))
		tw.addListener(adapter)
		onRun(tw);
	}
	refresh();
	def onRun(stream:TwitterStream):Unit;
	trait GeoListener {
		def onPosted(lng:Double, lat:Double, status:Status, usr:User);
	}
	val listeners:HashSet[GeoListener] = HashSet[GeoListener]()
	def addGeoListener (v: GeoListener) = {
		listeners.synchronized {
			listeners.add(v);
		}
		v
	}
	def removeGeoListener (v:GeoListener) = {
		listeners.synchronized {
			listeners.remove(v)
		}
	}
	private def onPosted(lng:Double, lat:Double, status:Status, usr:User):Unit = {
		listeners.synchronized {
			for(v<-listeners){
				v.onPosted(lng, lat, status, usr);
			}
		}
	}
	private var lastProcessed = System.currentTimeMillis();
	private def onPosted(status:Status):Unit = {
		val now = System.currentTimeMillis;
		val ok = synchronized {
			if( now-lastProcessed <= 1000 ) {
				false;
			}else{
				lastProcessed = now;
				true;
			}
		}
		if( ok ) {
			if(status.getGeoLocation() != null){
				val geo = status.getGeoLocation();
				onPosted(geo.getLongitude(), geo.getLatitude(), status, status.getUser());
			}else if(status.getPlace() != null){
				val pl = status.getPlace();
				var lng=0.0;
				var lat=0.0;
				var total=0;
				pl.getBoundingBoxCoordinates().foreach(x => x.foreach(t => {
					total+=1;
					lng += t.getLongitude();
					lat += t.getLatitude();
				}));
				onPosted(lng/total, lat/total, status, status.getUser());
			}
		}
	}
}