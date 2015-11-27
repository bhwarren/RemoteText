package me.bowarren.remotetext;

import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.UnrecoverableKeyException;
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
public class SMSServer {
    private Thread findThread;
    private Thread requestThread;

    private DatagramSocket connectSocket;
    private DatagramSocket requestSocket;

    private boolean stopFindThread = false;
    private boolean stopRequestThread = false;

    //private AsyncHttpClient client = new AsyncHttpClient();
    private int COMM_PORT = 9998;
    private int SEARCH_PORT = 9999;



    boolean computerIsConnected = false;
    String connectedComputerIp;

    //send the updated texts to the computer
    public void sendLatestTexts(){
    }


    //TODO: save mac address for auto connecting on difft networks
    //search for computer on local network
    //http://michieldemey.be/blog/network-discovery-using-udp-broadcast/
    public void findComputer() {
        findThread = new Thread(new Runnable() {
            public void run() {
                String ip = null;
                DatagramSocket socket;

                try {
                    //Keep a socket open to listen to all the UDP trafic that is destined for this port
                    connectSocket = new DatagramSocket(9999, InetAddress.getByName("0.0.0.0"));
                    connectSocket.setBroadcast(true);

                    while (true) {
                        System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        connectSocket.receive(packet);

                        //Packet received
                        System.out.println(getClass().getName() + ">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                        System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                        //See if the packet holds the right command (message)
                        String message = new String(packet.getData()).trim();
                        if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                            byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                            connectSocket.send(sendPacket);

                            recvBuf = new byte[15000];
                            packet = new DatagramPacket(recvBuf, recvBuf.length);
                            connectSocket.receive(packet);

                            String reply = new String(packet.getData()).trim();
                            Log.e("fffff",reply);
                            if(reply.equals("confirm2")){
                                sendData = "yes".getBytes();
                                sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                                connectSocket.send(sendPacket);
                            }
                        }
                    }
                } catch (IOException ex) {
                    //Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
                    Log.e("error in finding server", ex.toString());
                }
            }

        });
        findThread.start();
    }


    public void stopDiscovery(){
        if(connectSocket != null){
            connectSocket.close();
        }
        stopFindThread = true;

    }

    public void stop(){
        stopDiscovery();
        stopRequestThread = true;
        if(requestSocket != null) {
            requestSocket.close();
        }
    }


    public void listenForRequests(String ip){
        requestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Keep a socket open to listen to all the UDP trafic that is destined for this port
                    requestSocket = new DatagramSocket(COMM_PORT);
                    requestSocket.setBroadcast(true);

                    while(computerIsConnected && !stopRequestThread) {
                        System.out.println(getClass().getName() + ">>>Ready to receive packets on COMM_PORT!");

                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        requestSocket.receive(packet);

                        //Packet received
                        System.out.println(getClass().getName() + "received packet from: " + packet.getAddress().getHostAddress());
                        System.out.println(getClass().getName() + ">>>Packet received; data: " + new String(packet.getData()));

                        String clientIp = packet.getAddress().getHostAddress();
                        String clientData = new String(packet.getData()).trim();

                        doActionForRequest(clientIp, clientData);


                    }
                } catch (IOException ex) {
                    //Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE, null, ex);
                    Log.e("SMSServer:", ex.toString());
                }
            }
        });

        requestThread.start();
    }


    private void doActionForRequest(String originIp, String request){
//        if(request.equals()) {
//
//        }
//        else if(request.equals()){
//
//        }
//        else if(request.equals()){
//
//        }
    }




//    public class HTTPServer extends NanoHTTPD {
//        private int PORT;
//
//        public HTTPServer(int PORT) throws IOException {
//            super(PORT);
//            this.PORT = PORT;
//            start();
//            System.out.println( "\nRunning! Point your browers to http://localhost:8080/ \n" );
//        }
//
//
//        @Override
//        public Response serve(IHTTPSession session) {
//            String msg = "<html><body><h1>Hello server</h1>\n";
//            Map<String, String> parms = session.getParms();
//            if (parms.get("username") == null) {
//                msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
//            } else {
//                msg += "<p>Hello, " + parms.get("username") + "!</p>";
//            }
//            return newFixedLengthResponse( msg + "</body></html>\n" );
//        }
//    }
}



