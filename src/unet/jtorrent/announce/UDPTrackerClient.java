package unet.jtorrent.announce;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.net.trackers.udp.ResponseCallback;
import unet.jtorrent.net.trackers.udp.messages.AnnounceRequest;
import unet.jtorrent.net.trackers.udp.messages.AnnounceResponse;
import unet.jtorrent.net.trackers.udp.messages.ConnectRequest;
import unet.jtorrent.net.trackers.udp.messages.ConnectResponse;
import unet.jtorrent.announce.inter.AnnounceEvent;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;
import unet.jtorrent.announce.inter.TrackerClient;
import unet.jtorrent.utils.Torrent;

import java.io.IOException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UDPTrackerClient extends TrackerClient {
    private InetAddress[] addresses;
    private long[] connectionIDs;
    private int port, key;

    public UDPTrackerClient(TorrentClient client, Torrent torrent, URI uri)throws UnknownHostException, NoSuchAlgorithmException {
        super(client, torrent);

        addresses = InetAddress.getAllByName(uri.getHost());
        if(uri.getPort() != 0){
            port = uri.getPort();
        }
        connectionIDs = new long[addresses.length];

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        key = random.nextInt();
    }

    @Override
    public void announce(AnnounceEvent event){
        for(int i = 0; i < addresses.length; i++){
            announce(i, event);
        }
    }

    @Override
    public void scrape(){
        for(int i = 0; i < addresses.length; i++){

        }
    }

    private void connect(int i, AnnounceEvent event){
        ConnectRequest request = new ConnectRequest();
        request.setDestination(addresses[i], port);

        try{
            client.getUdpAnnounceSocket().send(request, new ResponseCallback(){
                @Override
                public void onResponse(MessageBase message){
                    ConnectResponse response = (ConnectResponse) message;
                    connectionIDs[i] = response.getConnectionID();
                    announce(i, event);
                }
            });

        }catch(IOException e){
            e.printStackTrace();
            System.out.println(addresses[i].getHostAddress()+"  "+addresses[i].getHostName());
        }
    }

    private void announce(int i, AnnounceEvent event){
        if(connectionIDs[i] == 0){
            connect(i, event);
            return;
        }

        AnnounceRequest request = new AnnounceRequest();
        request.setDestination(addresses[i], port);
        request.setConnectionID(connectionIDs[i]);
        request.setEvent(event);
        request.setInfoHash(torrent.getInfo().getHash());
        request.setPeerID(client.getPeerID()); //GRAB FROM CLIENT...
        request.setDownloaded(torrent.getDownloaded());
        request.setLeft(torrent.getLeft()); //MUST CALC THE AMMOUNT WE NEED...
        request.setUploaded(torrent.getUploaded());
        request.setNumWant(client.getMaxPeersPerRequest());
        request.setKey(key);
        request.setPort(client.getTCPPort());

        try{
            client.getUdpAnnounceSocket().send(request, new ResponseCallback(){
                @Override
                public void onResponse(MessageBase message){
                    AnnounceResponse response = (AnnounceResponse) message;
                    peers.addAll(response.getAllPeers());

                    for(InetSocketAddress address : response.getAllPeers()){
                        //System.out.println("UDP: "+address.getAddress().getHostAddress()+" : "+address.getPort());
                    }

                    //System.out.println("SEEDERS: "+response.getSeeders()+"  LEACHERS: "+response.getLeachers()+"  INTERVAL: "+response.getInterval());
                    System.out.println("UDP: "+response.getOrigin().getAddress().getHostAddress()+":"+response.getOrigin().getPort()+" GOT PEERS: "+peers.size());
                }
            });

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private byte[] stringToHex(String s){
        byte[] b = new byte[s.length()/2];
        for(int i = 0; i < b.length; i += 2){
            b[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+Character.digit(s.charAt(i+1), 16));
        }

        return b;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}