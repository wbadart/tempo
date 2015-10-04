var http = require('http');

//The url we want is: 'www.random.com/integers/?num=1&min=1&max=10&col=1&base=10&format=plain&rnd=new'
var options = {
  host: 'developer.echonest.com',
  path: '/api/v4/artist/news?api_key=B8YFO8YFTNJITHGWH&id=spotify:artist:5l8VQNuIg0turYE1VtM9zV&format=json'
};

callback = function(response) {
  var str = '';

  //another chunk of data has been recieved, so append it to `str`
  response.on('data', function (chunk) {
    str += chunk;
  });

  //the whole response has been recieved, so we just print it out here
  response.on('end', function () {
    console.log(str);
  });
}

http.request(options, callback).end();
