package unet.jtorrent.announce.inter;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.utils.Torrent;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class Tracker {

    protected TorrentClient client;
    protected Torrent torrent;
    protected List<InetSocketAddress> peers;

    public Tracker(TorrentClient client, Torrent torrent){
        this.client = client;
        this.torrent = torrent;
        peers = new ArrayList<>();
    }

    public void announce(){
        announce(AnnounceEvent.STARTED);
    }

    public abstract void announce(AnnounceEvent event);

    public abstract void scrape();

    public List<InetSocketAddress> getAllPeers(){
        return peers;
    }
}
