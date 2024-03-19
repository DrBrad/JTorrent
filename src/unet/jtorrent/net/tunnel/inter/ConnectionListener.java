package unet.jtorrent.net.tunnel.inter;

import java.net.InetSocketAddress;

public interface ConnectionListener {

    void onConnected(InetSocketAddress address);

    void onClosed(InetSocketAddress address);
}
