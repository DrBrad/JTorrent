package unet.jtorrent.utils.inter;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public abstract class Tracker {

    protected List<InetSocketAddress> peers;

    public Tracker(){
        peers = new ArrayList<>();
    }

    public abstract void announce();

    public abstract void scrape();

    public List<InetSocketAddress> getAllPeers(){
        return peers;
    }
}
