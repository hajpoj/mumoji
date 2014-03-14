var express = require('express');
var http = require('http');

var app = express();
app.set('port', process.env.PORT || 3000);

app.get('/', function(req, res) {
  res.send('hello world');
});

var server = http.createServer(app);

server.listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
