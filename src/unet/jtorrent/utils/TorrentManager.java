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
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static unet.jtorrent.TorrentClient.MAX_OPEN_CONNECTIONS;
import static unet.jtorrent.TorrentClient.MAX_RETRY_COUNT;

public class TorrentManager implements ConnectionListener, PeerListener {

    private TorrentClient client;
    private Torrent torrent;
    private File destination;

    private List<Tracker> trackers;
    private DownloadManager downloadManager;
    private ConcurrentLinkedQueue<Peer> peers, connected;

    public TorrentManager(TorrentClient client, Torrent torrent, File destination){
        this.client = client;
        this.torrent = torrent;
        this.destination = new File(destination, torrent.getInfo().getName());

        downloadManager = new DownloadManager(torrent);
        trackers = new ArrayList<>();
        peers = new ConcurrentLinkedQueue<>();
        connected = new ConcurrentLinkedQueue<>();
    }

    public void start(){
        createFiles();

        if(downloadManager.getLeft() == 0){
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
            tracker.addPeerListener(this);
            tracker.announce();
        }
    }

    private void createFiles(){
        for(TorrentFile f : torrent.getInfo().getFiles()){
            StringBuilder path = new StringBuilder();
            for(String p : f.getPath()){
                path.append("/"+p);
            }

            try{
                File file = new File(destination, path.toString());
                file.getParentFile().mkdirs();
                file.createNewFile();

                RandomAccessFile r = new RandomAccessFile(file, "rw");
                r.setLength(f.getLength());

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void openConnection(){
        if(connected.size() >= MAX_OPEN_CONNECTIONS){
            return;
        }

        if(downloadManager.getLeft() == 0){
            return;
        }

        Peer peer = peers.poll();
        connected.offer(peer);

        TCPSocket socket = new TCPSocket(this, peer);
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

    /*
    */

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
    public void onPeersReceived(List<Peer> peers){
        this.peers.addAll(peers);
        System.out.println("RECEIVED PEERS: "+getTotalPotentialPeers()+"  "+getTotalOpenConnections());

        for(int i = connected.size(); i < MAX_OPEN_CONNECTIONS; i++){
            openConnection();
        }
    }

    @Override
    public void onConnected(Peer peer){
    }

    @Override
    public void onClosed(Peer peer){
        peer.markStale();
        /*
        peer.markStale();

        if(peer.getStale() >= MAX_RETRY_COUNT){
            connected.remove(peer); //NOT NEEDED...

            if(peers.isEmpty()){
                for(Tracker tracker : trackers){
                    tracker.scrape();
                }

                //USE PEERS OBTAINED...

            }else{
                System.err.println("EXHAUSTED USING ALTERNATIVE PEER");
                openConnection(peers.get(0));
            }

            return;
        }

        System.err.println("RETRYING");
        openConnection(peer);
        */
        connected.remove(peer); //NOT NEEDED...

        if(!peers.isEmpty()){
            openConnection();
        }
    }
}
