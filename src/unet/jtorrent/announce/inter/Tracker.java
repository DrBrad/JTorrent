package unet.jtorrent.announce.inter;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.utils.TorrentManager;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class Tracker {

    protected TorrentManager manager;
    protected List<PeerListener> listeners;
    protected int peers = 0;

    public Tracker(TorrentManager manager){
        this.manager = manager;
        listeners = new ArrayList<>();
    }

    public void announce(){
        announce(AnnounceEvent.STARTED);
    }

    public abstract void announce(AnnounceEvent event);

    public abstract void scrape();

    public int getTotalPeers(){
        return peers;
    }

    public void addPeerListener(PeerListener listener){
        listeners.add(listener);
    }

    public void removePeerListener(PeerListener listener){
        listeners.remove(listener);
    }
}
