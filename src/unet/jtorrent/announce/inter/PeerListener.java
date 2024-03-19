package unet.jtorrent.announce.inter;

import java.net.InetSocketAddress;
import java.util.List;

public interface PeerListener {

    void onPeersReceived(List<InetSocketAddress> peers);
}
