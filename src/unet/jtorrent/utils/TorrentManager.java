package unet.jtorrent.utils;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.announce.HTTPTracker;
import unet.jtorrent.announce.UDPTracker;
import unet.jtorrent.announce.inter.PeerListener;
import unet.jtorrent.announce.inter.Tracker;
import unet.jtorrent.net.tunnel.inter.ConnectionListener;
import unet.jtorrent.net.tunnel.tcp.TCPSocket;
import unet.jtorrent.utils.inter.TrackerTypes;

import javax.sound.midi.Track;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static unet.jtorrent.TorrentClient.MAX_OPEN_CONNECTIONS;

public class TorrentManager implements ConnectionListener, PeerListener {

    private TorrentClient client;
    private Torrent torrent;

    private List<Tracker> trackers;
    private DownloadManager downloadManager;
    private List<InetSocketAddress> peers, connected;
    private long downloaded = 0, uploaded = 0;

    public TorrentManager(TorrentClient client, Torrent torrent){
        this.client = client;
        this.torrent = torrent;

        downloadManager = new DownloadManager(torrent.getInfo().getPieces());
        trackers = new ArrayList<>();
        peers = new ArrayList<>();
        connected = new ArrayList<>();
    }

    public void start(){
        if(getLeft() == 0){
            return;
        }

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
            tracker.addPeerListener(this);
            tracker.announce();
        }
    }

    private void openConnection(InetSocketAddress address){
        if(connected.size() >= MAX_OPEN_CONNECTIONS){
            return;
        }

        peers.remove(address);
        if(getLeft() == 0){
            return;
        }

        TCPSocket socket = new TCPSocket(this, address);
        socket.addConnectionListener(this);

        new Thread(socket).start();
    }

    /*
    public void closeConnection(InetSocketAddress address){
        if(connected.contains(address)){
            connected.get(address).close();
        }
    }

    public void closeAllConnections(){
        if(connected.isEmpty()){
            for(InetSocketAddress address : connected){

            }
        }
    }
    */

    public TorrentClient getClient(){
        return client;
    }

    public DownloadManager getDownloadManager(){
        return downloadManager;
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

    public int getTotalPotentialPeers(){
        return peers.size();
    }

    public int getTotalOpenConnections(){
        return connected.size();
    }

    @Override
    public void onPeersReceived(List<InetSocketAddress> peers){
        this.peers.addAll(peers);
        System.out.println("RECEIVED PEERS: "+this.peers.size());

        for(InetSocketAddress address : peers){
            openConnection(address);
        }
    }

    @Override
    public void onConnected(InetSocketAddress address){
        connected.add(address);
    }

    @Override
    public void onClosed(InetSocketAddress address){
        connected.remove(address);

        if(peers.isEmpty()){
            for(Tracker tracker : trackers){
                tracker.scrape();
            }

        }else{
            openConnection(peers.get(0));
        }
    }
}
