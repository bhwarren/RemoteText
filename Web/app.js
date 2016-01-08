var server = require('http').createServer();
var url = require('url');
var WebSocketServer = require('ws').Server;
var express = require('express');
var DBhelper = require('./DBhelper.js');

var app = express();
var port = 4080;
var wss = new WebSocketServer({ server: server });


app.use(function (req, res) {
  res.send({ msg: "hello" });
});

wss.on('connection', function connection(ws) {
    var location = url.parse(ws.upgradeReq.url, true);
    // you might use location.query.access_token to authenticate or share sessions
    // or ws.upgradeReq.headers.cookie (see http://stackoverflow.com/a/16395220/151312)
    ws.on('message', function incoming(message) {

        console.log('received: %s', message);

        try {
            message = JSON.parse(message);
        } catch (e) {
            console.error(e);
            ws.send("unrecognized command");
            return;
        }
        console.log(message.command);


        switch(message.command) {
            case "show":
                DBhelper.show(ws);
                break;
            case "clear":
                DBhelper.clear();
                break;
            case "upload":
                DBhelper.upload(message);
                break;
            case "compose":
                DBhelper.compose(message);
                break;
            case "showoutgoing":
                DBhelper.showOutgoing(ws);
                break;
            case "getnextoutgoing":
                DBhelper.getNextOutgoing(ws);
                break;
            case "rmnextoutgoing":
                DBhelper.rmNextOutgoing();
                break;
            default:
                ws.send("unrecognized command");
        }

    });

});

server.on('request', app);
server.listen(port, function () {
    console.log('Listening on ' + server.address().port);
});
