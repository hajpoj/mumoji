var express = require('express'),
    http = require('http'),
    sys = require('sys'),
    routes = require('./routes'),
    mongoose = require('mongoose');

var app = express();
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.engine('html', require('ejs').renderFile);
app.use(express.logger('dev'));
app.use(app.router);

// mongoose connection information:
mongoose.connect('mongodb://localhost/test');
var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function callback () {
    // yay!
});
var Schema = mongoose.Schema;
var estimoteSchema = new Schema({
    id: String,
    content: String
})
var Estimote = mongoose.model('Estimote', estimoteSchema);

var data = {
    "b9407f30-f5f8-466e-aff9-25556b57fe6d1889451784": "http://i.imgur.com/PibH1aY.jpg",
    "b9407f30-f5f8-466e-aff9-25556b57fe6d5695921068": "http://i.imgur.com/77yDzoh.jpg",
    "b9407f30-f5f8-466e-aff9-25556b57fe6d2967941577": "http://i.imgur.com/VzpGQne.jpg"
}

//app.get('/', function(req, res) {
//  res.send('<iframe width="560" height="315" src="//www.youtube.com/embed/9bZkp7q19f0?list=PLirAqAtl_h2r5g8xGajEwdXd3x1sZh8hC" frameborder="0" allowfullscreen></iframe>');
//});

app.get('/estimotes/new', function(req, res) {
    var estimoteOne = new Estimote({
        id: "b9407f30-f5f8-466e-aff9-25556b57fe6d1889451784",
        content: "http://i.imgur.com/PibH1aY.jpg"
    });

    estimoteOne.save(function (err, estimoteOne) {
        if (err) {
            return console.error(err);
        }
        else {
            console.log("great success!!");
        }
    });
    res.send("great success!");
});

app.get('/estimotes', function(req, res) {
    Estimote.find(function(err, estimotes) {
        if(err) {
            return console.error(err);
        }
        res.send(estimotes);
    })
});

app.get('/', routes.index);

app.get('/estimote', function(req, res) {
    var id = req.query.uuid + req.query.major + req.query.minor;
    res.send(data[id]);
});


var server = http.createServer(app);

server.listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});

