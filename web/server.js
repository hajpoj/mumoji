var express = require('express');
var http = require('http');

var app = express();
app.set('port', process.env.PORT || 3000);

app.get('/', function(req, res) {
  res.send('<iframe width="560" height="315" src="//www.youtube.com/embed/9bZkp7q19f0?list=PLirAqAtl_h2r5g8xGajEwdXd3x1sZh8hC" frameborder="0" allowfullscreen></iframe>');
});

app.get('/estimote', function(req, res) {
  res.send('http://www.youtube.com/watch?v=9bZkp7q19f0&feature=share&list=PLirAqAtl_h2r5g8xGajEwdXd3x1sZh8hC');
});

var server = http.createServer(app);

server.listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
