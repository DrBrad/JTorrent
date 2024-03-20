package unet.jtorrent.announce.inter;

import unet.jtorrent.utils.Peer;

import java.util.List;

public interface PeerListener {

    void onPeersReceived(List<Peer> peers);
}
