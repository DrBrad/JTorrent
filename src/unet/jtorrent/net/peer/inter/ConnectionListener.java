package unet.jtorrent.net.peer.inter;

public interface ConnectionListener {

    void onConnected(PeerSocket socket);

    void onClosed(PeerSocket socket);

    void onReadyToSend(PeerSocket socket);
}
