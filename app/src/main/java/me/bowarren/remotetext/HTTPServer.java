package me.bowarren.remotetext;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by bhwarren on 11/20/15.
 */
public class HTTPServer extends NanoHTTPD {
    private static int PORT;

    public HTTPServer(int PORT) throws IOException {
        super(PORT);
        this.PORT = PORT;
        start();
        System.out.println( "\nRunning! Point your browers to http://localhost:8080/ \n" );
    }


    @Override
    public Response serve(IHTTPSession session) {
        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        return newFixedLengthResponse( msg + "</body></html>\n" );
    }
}