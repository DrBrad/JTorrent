package unet.jtorrent;

import unet.jtorrent.trackers.http.client.HTTPTrackerClient;
import unet.jtorrent.trackers.udp.client.UDPAnnounceClient;
import unet.jtorrent.trackers.udp.client.UDPTrackerClient;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.trackers.inter.TrackerClient;
import unet.jtorrent.utils.inter.TrackerTypes;

import java.io.File;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class TorrentClient {

    private UDPAnnounceClient udp;
    private List<Torrent> torrents;
    private List<List<TrackerClient>> trackers;

    public TorrentClient(){
        torrents = new ArrayList<>();
        trackers = new ArrayList<>();
        udp = new UDPAnnounceClient();
        //download = new DownloadClient();
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
        List<TrackerClient> trackerList = new ArrayList<>();
        torrents.add(torrent);
        trackers.add(trackerList);

        System.out.println(bytesToHex(torrent.getInfo().getHash()));
        System.out.println(torrent.getInfo().getTotalLength());

        for(URI announce : torrent.getAnnounceList()){
            System.out.println(announce.toString());
            try{
                switch(TrackerTypes.getFromScheme(announce.getScheme())){
                    case UDP:
                        trackerList.add(new UDPTrackerClient(this, torrent, announce));
                        break;

                    case HTTP:
                    case HTTPS:
                        trackerList.add(new HTTPTrackerClient(this, torrent, announce));
                        break;
                }
            }catch(UnknownHostException | NoSuchAlgorithmException e){
                e.printStackTrace();
            }
        }

        for(TrackerClient tracker : trackerList){
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
        for(TrackerClient tracker : trackers.get(i)){
            tracker.announce();
        }
    }

    public List<TrackerClient> getPeers(int i){
        return trackers.get(i);
    }

    public UDPAnnounceClient getUDPClient(){
        return udp;
    }

    public byte[] getPeerID(){
        return new byte[]{ 0x2d, 0x54, 0x52, 0x33, 0x30, 0x30, 0x30, 0x2d, 0x32, 0x6f, 0x71, 0x72, 0x72, 0x70, 0x78, 0x62, 0x31, 0x30, 0x32, 0x32 };
    }

    public int getTCPPort(){
        return 6969;
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
