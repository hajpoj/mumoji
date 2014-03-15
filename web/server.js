var express = require('express');
var http = require('http');
var sys = require('sys');

var app = express();
app.set('port', process.env.PORT || 3000);

var data = {
    "b9407f30-f5f8-466e-aff9-25556b57fe6d1889451784": "http://www.youtube.com/watch?v=9bZkp7q19f0&feature=share&list=PLirAqAtl_h2r5g8xGajEwdXd3x1sZh8hC",
    "b9407f30-f5f8-466e-aff9-25556b57fe6d5695921068": "https://www.youtube.com/watch?v=oHg5SJYRHA0",
    "b9407f30-f5f8-466e-aff9-25556b57fe6d2967941577": "https://www.youtube.com/watch?v=GwCpnAndNQI&list=TL6qUrVp-md8-hPDdDWS9zrZjsaJm9QEoE"
}

app.get('/', function(req, res) {
  res.send('<iframe width="560" height="315" src="//www.youtube.com/embed/9bZkp7q19f0?list=PLirAqAtl_h2r5g8xGajEwdXd3x1sZh8hC" frameborder="0" allowfullscreen></iframe>');
});

app.get('/estimote', function(req, res) {
    var id = req.query.uuid + req.query.major + req.query.minor;
    res.send(data[id]);
});


var server = http.createServer(app);

server.listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});

