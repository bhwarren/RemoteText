package me.bowarren.remotetext;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by bhwarren on 11/20/15.
 */
public class TxtServer extends Service {

        public TxtServer() {
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onCreate() {
            Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onStart(Intent intent, int startId) {
            // For time consuming an long tasks you can launch a new thread here...
            Toast.makeText(this, " Service Started", Toast.LENGTH_LONG).show();

            NetworkHelper nh = new NetworkHelper();
            nh.startServer();


        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        }
    }

