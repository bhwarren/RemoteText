package me.bowarren.remotetext;

import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
    public void startDiscovery(){
         findThread = new Thread(new Runnable(){
            @Override
            public void run() {
                //Your code goes here
                String ip = null;

                try {
                    //Keep a socket open to listen to all the UDP trafic that is destined for this port
                    connectSocket = new DatagramSocket(SEARCH_PORT, InetAddress.getByName("0.0.0.0"));
                    connectSocket.setBroadcast(true);


                    while(!computerIsConnected && !stopFindThread) {
                        System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");

                        //Receive a packet
                        byte[] recvBuf = new byte[15000];
                        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                        connectSocket.receive(packet);

                        //Packet received
                        InetAddress clientAddress = packet.getAddress();
                        String clientData = new String(packet.getData()).trim();

                        System.out.println(getClass().getName() + ">>>Discovery packet received from: " + clientAddress.getHostAddress());
                        System.out.println(getClass().getName() + ">>>Packet received; data: " + clientData);


                        //See if the packet holds the right command (message)
                        if (clientData.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                            Log.e("", "got request, sent response");

                            String reply = sendForResponse("DISCOVER_FUIFSERVER_RESPONSE", clientAddress, SEARCH_PORT, connectSocket);
                            Log.e("", "this is the next reply (should be confirm2): "+reply);

                            if (reply.equals("confirm2")) {
                                boolean worked = sendToIp("yes", clientAddress, SEARCH_PORT, connectSocket);
                                if(worked) {
                                    computerIsConnected = stopFindThread = true;
                                    connectedComputerIp = ip;
                                    connectSocket.close();
                                    Log.e("connected to: ", connectedComputerIp);

                                    listenForRequests(connectedComputerIp);
                                }
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
        stopFindThread = false; //reset the boolean so can findthread can be started again later
    }

    private String sendForResponse(String data, InetAddress address, int port,  DatagramSocket socket){
        boolean worked = sendToIp(data, address, port, socket);

        if(!worked){
            Log.e("","failed to send to ip: "+address.getHostAddress());
            return null;
        }

        //try receiving, and return the response
        try{
            byte[] recvBuf = new byte[15000];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            socket.receive(packet);

            return new String(packet.getData());

        }
        catch(IOException e){
            Log.e("SMSServer:", e.toString());
            return null;
        }
    }

    //only send the data
    private boolean sendToIp(String data, InetAddress dest, int port,  DatagramSocket socket) {
//        try {
//            dest = InetAddress.getByName(ip);
//        } catch (UnknownHostException e) {
//            Log.e("SMSServer:", e.toString());
//            return false;
//        }


        byte[] sendData = data.getBytes();

        //Send a response that we've been found
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dest, port);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            Log.e("SMSServer:", e.toString());
            return false;
        }

        return true;
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



