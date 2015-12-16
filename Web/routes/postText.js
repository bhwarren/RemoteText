var express = require('express');
var router = express.Router();
var sqlite3 = require('sqlite3').verbose();


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
	response.send(request.body['username']);    // echo the result back
});

function show(response){
	var db = new sqlite3.Database(':TextDB:');
	  db.each("SELECT senderID AS id, message FROM texts", function(err, row) {
	      var msg = row.id + ": " + row.message;
		response.send(msg);
	  });
}

function upload(req){
	var db = new sqlite3.Database(':TextDB:');
	
	db.serialize(function() {
	  db.run("CREATE TABLE IF NOT EXISTS contacts (senderID INTEGER PRIMARY KEY, sender TEXT, UNIQUE(senderID, sender))");
	  db.run("CREATE TABLE IF NOT EXISTS texts (senderID INTEGER,  message TEXT, FOREIGN KEY(senderID) REFERENCES contacts(senderID))");

	  var un = req.body['sender'];
	  db.run("INSERT INTO contacts('sender') VALUES ('"+un+"')");
	 
	   db.each("SELECT senderID AS id, sender FROM contacts WHERE sender = '"+un+"' LIMIT 1", function(err, row) {
      		console.log(row.id + ": " + row.sender);
	  	var msg = req.body['message'];

		  db.run("INSERT INTO texts VALUES ("+row.id+",'"+msg+"')");
		return;
	  });

	
	});
	//db.close();
	
}

function clear(){
	var db = new sqlite3.Database(':TextDB:');
	db.run('DROP TABLE IF EXISTS lorem');
	db.run('DROP TABLE IF EXISTS contacts');
	db.run('DROP TABLE IF EXISTS texts');

	db.close();
}


module.exports = router;
