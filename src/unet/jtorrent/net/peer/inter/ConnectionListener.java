package unet.jtorrent.net.peer.inter;

import unet.jtorrent.utils.Peer;

public interface ConnectionListener {

    void onConnected(PeerSocket socket);

    void onClosed(PeerSocket socket);
}
