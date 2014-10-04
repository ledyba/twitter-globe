var map;
$(function() {
	function Map(map){
		var spr = {map:map};
		var z = 0;
		spr.addMarker = function(dat){
			var lat = dat.lat;
			var lng = dat.lng;
			var msg = dat.msg;
			var lastP = new google.maps.LatLng(lat,lng)
			var url = "https://twitter.com/"+dat.usr;
			var image = new Image();

			image.onload = function () {
				var $d = $("<div/>").css("width", 300);
				$d.append($("<a/>").attr("target","_blank").attr("href", url).append( $(image).css("margin", 5).css("float", "left") ))
				$d.append($("<div/>").append($("<a/>").attr("target","_blank").text(dat.usr).attr("href", url)))
				$d.append($("<div/>").text(msg))
				$d.append($("<div/>").append($("<span/>").text("from ")).append($("<span/>").html(dat.client)))
				$d.append($("<div/>").css("clear", "left"))
				var infoD = new google.maps.InfoWindow({
					disableAutoPan: true,
					content:$d[0],
					position:lastP
				});
				infoD.setZIndex(z);
				++z;
				infoD.open(map);
				setTimeout( function() {
					infoD.close();
				}, 5000);
			};
			image.src = dat.image;
		}
		return spr;
	}
	function initialize() {
		var mapOptions = {
				zoom: 2,
				center: new google.maps.LatLng(0, 145)
		};
		map = Map(new google.maps.Map(document.getElementById('map-canvas'), mapOptions));
	}
	google.maps.event.addDomListener(window, 'load', initialize);
});

$(function() {
  "use strict";
  var socket = $.atmosphere;
  var subSocket;
  var transport = 'websocket';

  var request = {
    url: "public",
    contentType: "application/json",
    logLevel: 'debug',
    transport: transport,
    fallbackTransport: 'long-polling'
  };

  request.onOpen = function(response) {
    console.log("Connection opened.");
  };

  request.onReconnect = function(rq, rs) {
    console.log("Connection reconnected.");
  };

  request.onMessage = function(rs) {
    var message = rs.responseBody;
    try {
      var json = jQuery.parseJSON(message);
      map.addMarker(json);
    } catch (e) {
        console.log(e)
    }
  };

  request.onClose = function(rs) {
    console.log("Connection closed.");
  };

  request.onError = function(rs) {
    console.log("Connection Error: "+rs);
    content.html($('<p>', {
      text: 'Sorry, but there\'s some problem with your socket or the server is down'
    }));
  };

  subSocket = socket.subscribe(request);
});
