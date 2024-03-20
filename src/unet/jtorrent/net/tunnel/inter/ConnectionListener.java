package unet.jtorrent.net.tunnel.inter;

import unet.jtorrent.utils.Peer;

public interface ConnectionListener {

    void onConnected(Peer peer);

    void onClosed(Peer peer);
}
