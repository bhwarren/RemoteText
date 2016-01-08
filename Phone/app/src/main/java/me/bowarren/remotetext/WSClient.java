package me.bowarren.remotetext;


import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;


public class WSClient extends WebSocketClient {

    public WSClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
        super.connect();
    }

    public WSClient(URI serverURI) {
        super(serverURI);
        super.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("remotetext", "opened connection");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
        super.send("asdfasdf");
    }

    @Override
    public void onMessage(String message) {
        Log.e("remotetext",  "received: " + message);

    }


//    @Override
//    public void onFragment( Framedata fragment) {
//        System.out.println("received fragment: " + new String(fragment.getPayloadData().array() ) );
//    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        Log.e("remotetext", "Connection closed by " + ( remote ? "remote peer" : "us" ));
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

}