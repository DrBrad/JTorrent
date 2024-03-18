package unet.jtorrent.trackers.udp;

import unet.jtorrent.trackers.udp.messages.inter.MessageBase;

public interface ResponseCallback {

    void onResponse(MessageBase message);
}
