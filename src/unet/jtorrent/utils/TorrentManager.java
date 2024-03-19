package unet.jtorrent.utils;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.announce.HTTPTracker;
import unet.jtorrent.announce.UDPTracker;
import unet.jtorrent.announce.inter.PeerListener;
import unet.jtorrent.announce.inter.Tracker;
import unet.jtorrent.net.tunnel.tcp.TCPSocket;
import unet.jtorrent.utils.inter.TrackerTypes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static unet.jtorrent.TorrentClient.MAX_OPEN_CONNECTIONS;

public class TorrentManager {

    private TorrentClient client;
    private Torrent torrent;

    private List<Tracker> trackers;
    private List<InetSocketAddress> connections;
    private long downloaded = 0, uploaded = 0;

    public TorrentManager(TorrentClient client, Torrent torrent){
        this.client = client;
        this.torrent = torrent;
        trackers = new ArrayList<>();
        connections = new ArrayList<>();
    }

    public void start(){
        for(URI announce : torrent.getAnnounceList()){
            System.out.println(announce.toString());
            try{
                switch(TrackerTypes.getFromScheme(announce.getScheme())){
                    case UDP:
                        trackers.add(new UDPTracker(this, announce));
                        break;

                    case HTTP:
                    case HTTPS:
                        trackers.add(new HTTPTracker(this, announce));
                        break;
                }
            }catch(UnknownHostException | NoSuchAlgorithmException e){
                e.printStackTrace();
            }
        }

        for(Tracker tracker : trackers){
            //LISTEN FOR PEERS... -> THEN START DOWNLOADING
            tracker.addPeerListener(new PeerListener(){
                @Override
                public void onPeersReceived(List<InetSocketAddress> peers){
                    for(InetSocketAddress address : peers){
                        if(getTotalOpenConnections() >= MAX_OPEN_CONNECTIONS){
                            break;
                        }

                        try{
                            System.out.println("TCP CONNECT TO: "+address.getAddress().getHostAddress()+":"+address.getPort());
                            openConnection(address);
                        }catch(IOException e){
                            e.printStackTrace();
                            connections.remove(address);
                        }
                    }
                }
            });
            tracker.announce();
        }
    }

    private void openConnection(InetSocketAddress address)throws IOException {
        connections.add(address);

        new Thread(new TCPSocket(this, address)).start();
    }

    public TorrentClient getClient(){
        return client;
    }

    public long getDownloaded(){
        return downloaded;
    }

    public long getLeft(){
        return downloaded-torrent.getInfo().getTotalLength();
    }

    public long getUploaded(){
        return uploaded;
    }

    public Torrent getTorrent(){
        return torrent;
    }

    public synchronized int pollPiece(){
        //POLL WILL TAKE INCOMPLETE LIST HASH AND MOVE IT TO WORKING ON
        //

        //piecesCompleted[i] = true;

        return 0; //RETURN INDEX
    }

    public synchronized void completedPiece(int i){
        //REMOVE PIECE FROM INCOMPLETE
    }

    public synchronized void restorePiece(int i){
        //IF PIECE FAILED DO TO (EXAMPLE: BROKEN SOCKET) REDO THE PIECE
    }

    public synchronized void verify(){
        //VERIFY ALL OF THE PIECES...
    }

    public synchronized int getTotalPotentialPeers(){
        int i = 0;
        for(Tracker tracker : trackers){
            i += tracker.getTotalPeers();
        }
        return i;
    }

    public synchronized int getTotalOpenConnections(){
        return connections.size();
    }
}
