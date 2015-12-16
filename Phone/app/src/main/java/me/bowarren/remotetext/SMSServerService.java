package me.bowarren.remotetext;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by bhwarren on 11/20/15.
 */
public class SMSServerService extends Service {

    SMSServer server;


    public SMSServerService() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        server = new SMSServer();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        Toast.makeText(this, " Service Started, waiting for a connection", Toast.LENGTH_LONG).show();

        //starts listening for requests after a client connects
        server.findComputer();


    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        server.stop();
    }
}

