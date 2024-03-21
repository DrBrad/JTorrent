package unet.jtorrent.utils;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.announce.HTTPTracker;
import unet.jtorrent.announce.UDPTracker;
import unet.jtorrent.announce.inter.PeerListener;
import unet.jtorrent.announce.inter.Tracker;
import unet.jtorrent.utils.inter.TrackerTypes;

import java.io.File;
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
    private DownloadManager downloadManager;
    private ConnectionManager connectionManager;

    public TorrentManager(TorrentClient client, Torrent torrent, File destination){
        this.client = client;
        this.torrent = torrent;

        downloadManager = new DownloadManager(this, destination);
        connectionManager = new ConnectionManager(this);
        trackers = new ArrayList<>();
    }

    public void start(){
        downloadManager.createFiles();

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
            tracker.addPeerListener(new PeerListener(){
                @Override
                public void onPeersReceived(List<Peer> peers){
                    connectionManager.addPeers(peers);

                    for(int i = connectionManager.getTotalOpenConnections(); i < MAX_OPEN_CONNECTIONS; i++){
                        connectionManager.connectToPeer();
                    }
                }
            });
            tracker.announce();
        }
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

    public ConnectionManager getConnectionManager(){
        return connectionManager;
    }

    public Torrent getTorrent(){
        return torrent;
    }
}
