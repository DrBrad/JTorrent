package unet.jtorrent.trackers.inter;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.utils.Torrent;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class TrackerClient {

    protected TorrentClient client;
    protected Torrent torrent;
    protected List<InetSocketAddress> peers;
    protected int port = 6969, numWant = -1;

    public TrackerClient(TorrentClient client, Torrent torrent){
        this.client = client;
        this.torrent = torrent;
        peers = new ArrayList<>();
    }

    public abstract void announce();

    public abstract void scrape();

    public List<InetSocketAddress> getAllPeers(){
        return peers;
    }
}
