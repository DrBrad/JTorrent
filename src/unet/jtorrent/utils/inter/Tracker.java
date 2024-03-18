package unet.jtorrent.utils.inter;

import unet.jtorrent.utils.Peer;

import java.util.ArrayList;
import java.util.List;

public abstract class Tracker {

    private List<Peer> peers;

    public Tracker(){
        peers = new ArrayList<>();
    }

    public abstract void announce();

    public abstract void scrape();
}
