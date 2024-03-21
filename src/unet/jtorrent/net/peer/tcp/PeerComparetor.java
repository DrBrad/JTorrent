package unet.jtorrent.net.peer.tcp;

import unet.jtorrent.utils.Peer;

import java.util.Comparator;

public class PeerComparetor implements Comparator<Peer> {

    @Override
    public int compare(Peer a, Peer b){
        return (a.hashCode() == b.hashCode()) ? 0 : (a.getStale() > b.getStale()) ? 1 : -1;
    }
}
