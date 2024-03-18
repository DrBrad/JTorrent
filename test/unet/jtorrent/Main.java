package unet.jtorrent;

import unet.jtorrent.trackers.udp.messages.AnnounceResponse;
import unet.jtorrent.utils.UDPTracker;
import unet.jtorrent.trackers.udp.client.UDPClient;
import unet.jtorrent.utils.Torrent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args)throws Exception {
        /*
        Kademlia kad = new Kademlia();

        //BEP 5
        kad.registerMessage(AnnouncePeerRequest.class);
        kad.registerMessage(AnnouncePeerResponse.class);

        //BEP 5
        kad.registerMessage(GetRequest.class);
        kad.registerMessage(GetResponse.class);

        //BEP 5
        kad.registerMessage(PutRequest.class);
        kad.registerMessage(PutResponse.class);

        //BEP 5
        kad.registerMessage(GetPeersRequest.class);
        kad.registerMessage(GetPeersResponse.class);

        //BEP 51
        kad.registerMessage(SampleHashRequest.class);
        kad.registerMessage(SampleHashResponse.class);
        */

        TorrentManager manager = new TorrentManager();
        manager.start();

        manager.startTorrent(new File("/home/brad/Downloads/torrent.torrent"));


        /*
        Torrent torrent = new Torrent(new File("/home/brad/Downloads/torrent.torrent"));

        System.out.println(torrent.getComment());
        System.out.println(torrent.getCreatedBy());
        System.out.println(torrent.getInfo().getFiles().get(0).getPath().get(0));

        StringBuilder builder = new StringBuilder();
        for(byte b : torrent.getInfo().getHash()){
            builder.append(String.format("%02x", b));
        }

        //2 751 421 688 LEFT
        //NUM_WANT 80...? WHY NOT -1
        //KEY SHOULD BE STORED BY TRACKER... - AS IPv4 & IPv6 SHOULD USE SAME KEY IF ANNOUNCING TO BOTH...

        System.out.println(torrent.getInfo().getPieces().size()+"  "+torrent.getInfo().getTotalLength());

        UDPClient client = new UDPClient();
        client.start();

        for(URI uri : torrent.getAnnounceList()){
            switch(uri.getScheme()){
                case "udp":

                    break;

                case "http":

                    break;
            }
        }




        //MAKE TRACKER SERVER OPTION...



        /*
        for(byte[] piece : torrent.getInfo().getPieces()){
            System.out.println("PIECE: "+bytesToHex(piece));
        }
        */


        /*

        //new DHTTracker(torrent.getInfoHash());

        UDPClient client = new UDPClient();
        try{
            client.start(6969);

            for(String announce : torrent.getAnnounceList()){
                try{
                    URI uri = new URI(announce);
                    if(!uri.getScheme().equals("udp")){
                        continue;
                    }

                    System.out.println("UDP SENDING:  "+InetAddress.getByName(uri.getHost()).getHostAddress()+"  "+uri.getPort());

                    ConnectRequest request = new ConnectRequest();
                    request.setDestination(InetAddress.getByName(uri.getHost()), uri.getPort());
                    client.send(request, new ResponseCallback(){
                        @Override
                        public void onResponse(MessageBase message){
                            ConnectResponse response = (ConnectResponse) message;
                            System.out.println("RESPONSE: "+response.getConnectionID());

                            //DO THE REQUESTS BETTER...?
                            AnnounceRequest request1 = new AnnounceRequest();
                            request1.setDestination(request.getDestination());
                            request1.setConnectionID(response.getConnectionID());
                            request1.setEvent(AnnounceEvent.STARTED);
                            request1.setInfoHash(torrent.getInfo().getHash()); //9e8cb640823822be312c1278089c96cafacc8627
                            request1.setPeerID(stringToHex("2d5452333030302d326f71727270786231303232"));
                            request1.setDownloaded(0);
                            request1.setLeft(1880132108); //MUST CALC THE AMMOUNT WE NEED...
                            request1.setUploaded(0);
                            request1.setNumWant(80);
                            request1.setKey(330182370);
                            request1.setPort(8080); //TCP PORT

                            try{
                                client.send(request1, new ResponseCallback(){
                                    @Override
                                    public void onResponse(MessageBase message){
                                        AnnounceResponse response = (AnnounceResponse) message;

                                        System.out.println(response.getInterval()+"  "+response.getLeachers()+"  "+response.getSeeders());
                                    }
                                });
                            }catch(IOException ex){
                                ex.printStackTrace();
                            }

                        }
                    });
                }catch(URISyntaxException ex){
                    ex.printStackTrace();
                }
            }

        }catch(IOException ex){
            ex.printStackTrace();
        }
        */
    }

    public static void test(){
        // The announce URL of the tracker
        String announceUrl = "http://tracker.example.com/announce";

        // Construct the announce URL with required parameters
        String infoHash = "your_info_hash";
        String peerId = "your_peer_id";
        long uploaded = 0;
        long downloaded = 0;
        long left = 100; // Example value, adjust as needed
        String event = "started"; // Example value, adjust as needed
        int port = 6881; // Example value, adjust as needed

        String announceRequest = "?info_hash="+infoHash+
                "&peer_id="+peerId+
                "&downloaded="+downloaded+
                "&left="+left+
                "&uploaded="+uploaded+
                "&event="+event+
                "&port="+port;
        //String announceRequest = String.format("%s?info_hash=%s&peer_id=%s&uploaded=%s&downloaded=%s&left=%s&event=%s&port=%s",
        //        announceUrl, infoHash, peerId, uploaded, downloaded, left, event, port);

        try {
            // Create a URL object from the announce request string
            URL url = new URL(announceRequest);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set request method to GET
            connection.setRequestMethod("GET");

            // Read the response from the tracker
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Print the response from the tracker
            System.out.println("Tracker response: " + response.toString());

            // Close the connection
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
