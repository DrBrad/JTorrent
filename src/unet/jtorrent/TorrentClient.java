package unet.jtorrent;

import unet.jtorrent.net.trackers.udp.client.UDPTrackerSocket;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.utils.TorrentManager;

import java.io.File;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TorrentClient {

    public static final int MAX_OPEN_CONNECTIONS = 50;
    private UDPTrackerSocket udpAnnounce;
    private List<TorrentManager> torrents;
    private int maxPeersPerRequest = -1;

    public TorrentClient(){
        torrents = new ArrayList<>();
        udpAnnounce = new UDPTrackerSocket();
        //download = new DownloadClient();
    }

    public synchronized void start()throws SocketException {
        if(!udpAnnounce.isRunning()){
            udpAnnounce.start();
        }
    }

    public synchronized void stop(){
        if(udpAnnounce.isRunning()){
            udpAnnounce.stop();
        }
    }

    public synchronized void startTorrent(File source, File destination){
        startTorrent(new Torrent(source), destination);
    }

    public synchronized void startTorrent(Torrent torrent, File destination){
        TorrentManager manager = new TorrentManager(this, torrent, destination);
        manager.start();
        torrents.add(manager);

        System.out.println(bytesToHex(torrent.getInfo().getHash()));
        System.out.println(torrent.getInfo().getTotalLength());

        //ONCE WE GET PEERS START DOWNLOADING...

    }

    public synchronized void resumeTorrent(int i){

    }

    public synchronized void pauseTorrent(int i){

    }

    public synchronized void stopTorrent(int i){

    }

    public synchronized void removeTorrent(int i){
        torrents.remove(i);
    }

    public synchronized void requestMorePeers(int i){
        //for(Tracker tracker : trackers.get(i)){
        //    tracker.announce();
        //}
    }

    /*
    public List<Tracker> getPeers(int i){
        return trackers.get(i);
    }
    */

    public UDPTrackerSocket getUdpAnnounceSocket(){
        return udpAnnounce;
    }

    public int getMaxPeersPerRequest(){
        return maxPeersPerRequest;
    }

    public void setMaxPeersPerRequest(int maxPeersPerRequest){
        this.maxPeersPerRequest = maxPeersPerRequest;
    }

    public byte[] getPeerID(){
        return new byte[]{ 0x2d, 0x54, 0x52, 0x53, 0x30, 0x30, 0x30, 0x2d, 0x32, 0x6f, 0x71, 0x72, 0x72, 0x70, 0x78, 0x62, 0x31, 0x30, 0x32, 0x32 };
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
