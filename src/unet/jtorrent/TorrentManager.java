package unet.jtorrent;

import unet.jtorrent.trackers.udp.client.UDPClient;
import unet.jtorrent.utils.Torrent;

import java.io.File;
import java.net.URI;

public class TorrentManager {

    private UDPClient udp;

    public TorrentManager(){
        udp = new UDPClient();
    }

    public void startTorrent(File source, File dest){
        Torrent torrent = new Torrent(source);

        for(URI announce : torrent.getAnnounceList()){
            switch(announce.getScheme()){
                case "udp":

                    break;

                case "http":

                    break;
            }
        }
    }

    public void resumeTorrent(){

    }

    public void pauseTorrent(){

    }

    public void stopTorrent(){

    }

    public UDPClient getUDPClient(){
        return udp;
    }
}
