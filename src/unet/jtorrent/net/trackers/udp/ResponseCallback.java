package unet.jtorrent.net.trackers.udp;

import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;
import unet.jtorrent.net.trackers.udp.messages.ErrorResponse;

public abstract class ResponseCallback {

    public abstract void onResponse(MessageBase message);

    public void onErrorResponse(ErrorResponse message){
    }
}
