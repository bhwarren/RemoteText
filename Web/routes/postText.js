var express = require('express');
var router = express.Router();
var sqlite3 = require('sqlite3').verbose();

var db = new sqlite3.Database(':TextDB:');
createTablesIfNeeded();


/* GET home page. */
router.get('/', function(req, res, next) {
	show(res);
});
router.get('/clear', function(req, res, next) {
	clear();
	res.send("done");
});
router.post('/upload',  function(request, response) {
	upload(request);
	response.send(request.body.username);    // echo the result back
});
router.post('/compose', function(request, response){
	compose(request);
	response.send("sent?");
});
router.get('/showoutgoing', function(request, response){
	db.run("CREATE TABLE IF NOT EXISTS outqueue (contactID INTEGER, message TEXT, FOREIGN KEY(contactID) REFERENCES contacts(senderID))");
	db.all("SELECT msgID, sender, message FROM outqueue o INNER JOIN contacts c ON o.contactID = c.senderID  ORDER BY sender", function(err, rows) {

		var msg = "";
		if(rows === undefined){
			response.send("none");
			return;
		}
		for (i = 0; i < rows.length; i++) {
			var row = rows[i];
		  	msg = msg + row.msgID + row.sender + ": " + row.message + "<br>";
		}
		response.send(msg);
	});
});

router.get('/nextoutgoing', function(request, response){
	getNextOutgoing(request, response);
});
router.get('/rmnextoutgoing', function(request, response){
	rmNextOutgoing();
	response.send("done");
});


function createTablesIfNeeded(){
	db.serialize(function() {
		db.run("CREATE TABLE IF NOT EXISTS contacts (senderID INTEGER PRIMARY KEY, sender TEXT, UNIQUE(senderID, sender))");
		db.run("CREATE TABLE IF NOT EXISTS outqueue (msgID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, contactID INTEGER, message TEXT, FOREIGN KEY(contactID) REFERENCES contacts(senderID))");
		db.run("CREATE TABLE IF NOT EXISTS texts (senderID INTEGER,  message TEXT, FOREIGN KEY(senderID) REFERENCES contacts(senderID))");
	});
}

function show(response){
	  db.all("SELECT sender, message FROM texts t INNER JOIN contacts c ON t.senderID = c.senderID  ORDER BY sender", function(err, rows) {
		  if(rows === undefined){
			  response.send("no results");
			  return;
		  }
	      var msg = "";
		  for (i = 0; i < rows.length; i++) {
			  var row = rows[i];
			  msg = msg + row.sender + ": " + row.message + "<br>";
		  }
		  response.send(msg);
	  });
}

function upload(req){

	if(!("sender" in req.body) || !("message" in req.body)){
		return;
	}


	db.serialize(function() {

	  var un = req.body.sender;
	  db.run("INSERT INTO contacts ('sender') VALUES (?)", [un]);

	  db.each("SELECT senderID AS id, sender FROM contacts WHERE sender = ? LIMIT 1", [un], function(err, row) {
      		console.log(row.id + ": " + row.sender);
	  		var msg = req.body.message;

		  	db.run("INSERT INTO texts VALUES (?,?)", [row.id, msg]);
			return;
	  });


	});
	//db.close();

}

function clear(){
	db.run('DROP TABLE IF EXISTS lorem');
	db.run('DROP TABLE IF EXISTS contacts');
	db.run('DROP TABLE IF EXISTS texts');
	db.run('DROP TABLE IF EXISTS outqueue');
}

function compose(req){

	if(!("recipient" in req.body) || !("message" in req.body)){
		return;
	}

	db.serialize(function() {

	  	var recipient = req.body.recipient;
  		var msg = req.body.message;

		db.run("INSERT INTO contacts ('sender') VALUES (?)", [recipient]);
		db.run("INSERT INTO outqueue ('contactID', 'message') VALUES ((SELECT senderID FROM contacts c WHERE c.sender = ?),?)", [recipient, msg]);

	});
}

function getNextOutgoing(req, resp){

	db.get("SELECT sender, message FROM outqueue o INNER JOIN contacts c ON o.contactID = c.senderID", function(err, row) {
		if(row === undefined){
			return;
		}
		var responseMessage = {"contact ": row.sender, 	"message": row.message};
		resp.send(JSON.stringify(responseMessage));
	});
}

function rmNextOutgoing(){
	console.log("rmd");
	db.run("DELETE FROM outqueue where msgID = (SELECT MIN(msgID) FROM outqueue) ");
}


module.exports = router;
