package me.bowarren.remotetext;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.java_websocket.drafts.Draft_17;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by bhwarren on 11/20/15.
 */
public class SMSService extends Service {

    WSClient wsclient;
    String serverLocation = "ws://bowarren.me/RemoteText";

    public SMSService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();

        try {
            WSClient wsClient = new WSClient(new URI(serverLocation), new Draft_17()); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
        }
        catch(URISyntaxException e){
            Toast.makeText(this, "Invalid URI for server\n" +e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, " Service Started, waiting for a connection", Toast.LENGTH_LONG).show();

        //starts listening for requests after a client connects
        //server.findComputer();
//        if(wsclient != null){
//            wsclient.send("hi");
//            Log.e("remotetext", "hi is sent");
//        }



    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if(wsclient != null){
            wsclient.close();
        }
        //server.stop();
    }
}

