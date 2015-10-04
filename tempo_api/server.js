var http = require('http');

var server = http.createServer(function(request, response){
	var form_response = function(msg){
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.writeHead(200, {"Content-Type":"text/plain"});
		response.write(msg);
		response.end();
	};
	
	var is_valid = new RegExp("\}\]\}\]\}\}$|\}\]\}\}$");

	request.on('readable', function(){
		var input = request.read().toString();
		console.log("POST Request @" + new Date().toString() + ": " + input);
		var req = "/api/v4/song/search?api_key=B8YFO8YFTNJITHGWH&bucket=id:spotify&bucket=tracks&max_tempo=";
		var min = parseInt(input, 10) - 5;
		var max = parseInt(input, 10) + 5;
		req += max.toString() + "&min_tempo=" + min.toString();
		
		http.get({
			host:'developer.echonest.com',
			path:req,
		}, function(res){
			res.on('data', function(d){
				var data = d.toString();
				var go = true;
				if(!is_valid.test(data) || !data.indexOf("\"code\": 3,")){
					console.log("\tincomplete object");
					console.log("\t" + data);
					go = false;
				}
				data = JSON.parse(data).response;
				var msg = "";
				if(!data.hasOwnProperty("songs") || data.songs.length <= 0) go = false;
				if(go) var i = Math.floor(Math.random() * data.songs.length);
				if(!data.status.code && go && data.songs[i].hasOwnProperty("tracks") && data.songs[i].tracks.length > 0){
					msg = data.songs[i].tracks[0].foreign_id + " "  + data.songs[i].title + " by " + data.songs[i].artist_name;
				}else{
					msg = "-1";
				}
				console.log("\tResponse: " + msg);
				form_response(msg);
			});
		});
	});
}).listen(80);
