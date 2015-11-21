package me.bowarren.remotetext;

import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import fi.iki.elonen.NanoHTTPD;


/**
 * Created by bhwarren on 11/20/15.
 */
public class NetworkHelper {
    private AsyncHttpClient client = new AsyncHttpClient();
    public int PORT = 8888;


    boolean computerIsConnected = false;
    String connectedComputerIp;

    //send the updated texts to the computer
    public void sendLatestTexts(){
        if(computerIsConnected){
            //do the sending
            JSONObject content = SMSHelper.getLatestTexts();

            RequestParams params = new RequestParams();
            params.put("content", content );
            client.post(connectedComputerIp, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                }
            });


        }
    }

    //start the http server
    public void startServer(){
        try {
            HTTPServer messageServer = new HTTPServer(PORT);
        }
        catch(IOException e){
            Log.e("nanohttpd", e.toString());
        }


    }


    //TODO: save mac address for auto connecting on difft networks
    //search for computer on local network
    //http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
    public String findComputer(){
        String ip = null;
        DatagramSocket socket;

        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //Packet received
                System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)
                String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    System.out.println(getClass().getName() + ">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                    socket.close();

                    ip = sendPacket.getAddress().getHostAddress();
                }
            }
        } catch (IOException ex) {
            //Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
            Log.e("error in finding server", ex.toString());
        }

        return ip;
    }



}



