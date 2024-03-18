package unet.jtorrent;

import unet.jtorrent.trackers.udp.client.UDPClient;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.utils.UDPTracker;
import unet.jtorrent.utils.inter.Tracker;
import unet.jtorrent.utils.inter.TrackerTypes;

import java.io.File;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class TorrentManager {

    private UDPClient udp;
    private List<Torrent> torrents;
    private List<List<Tracker>> trackers;

    public TorrentManager(){
        torrents = new ArrayList<>();
        trackers = new ArrayList<>();
        udp = new UDPClient();
    }

    public void start()throws SocketException {
        if(!udp.isRunning()){
            udp.start();
        }
    }

    public void stop(){
        if(udp.isRunning()){
            udp.stop();
        }
    }

    public void startTorrent(File source/*, File dest*/){
        startTorrent(new Torrent(source));
    }

    public void startTorrent(Torrent torrent){
        List<Tracker> trackerList = new ArrayList<>();
        torrents.add(torrent);
        trackers.add(trackerList);

        for(URI announce : torrent.getAnnounceList()){
            try{
                switch(TrackerTypes.getFromScheme(announce.getScheme())){
                    case UDP:
                        trackerList.add(new UDPTracker(this, torrent, announce));
                        break;

                    case HTTP:

                        break;
                }
            }catch(UnknownHostException | NoSuchAlgorithmException e){
                e.printStackTrace();
            }
        }

        for(Tracker tracker : trackerList){
            tracker.announce();
        }

        //ONCE WE GET PEERS START DOWNLOADING...

    }

    public void resumeTorrent(int i){

    }

    public void pauseTorrent(int i){

    }

    public void stopTorrent(int i){

    }

    public void removeTorrent(int i){
        torrents.remove(i);
        trackers.remove(i);
    }

    public void requestMorePeers(int i){
        for(Tracker tracker : trackers.get(i)){
            tracker.announce();
        }
    }

    public List<Tracker> getPeers(int i){
        return trackers.get(i);
    }

    public UDPClient getUDPClient(){
        return udp;
    }
}
