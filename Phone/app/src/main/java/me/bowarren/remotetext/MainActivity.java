package me.bowarren.remotetext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }

        // Start the  service
        public void startNewService(View view) {
            Toast.makeText(this, "starting service", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, SMSService.class));
        }

        // Stop the  service
        public void stopNewService(View view) {
            Toast.makeText(this, "stopping service", Toast.LENGTH_SHORT).show();
            stopService(new Intent(this, SMSService.class));
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

}
