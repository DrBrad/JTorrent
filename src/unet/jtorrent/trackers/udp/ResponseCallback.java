package unet.jtorrent.trackers.udp;

import unet.jtorrent.trackers.udp.messages.ErrorResponse;
import unet.jtorrent.trackers.udp.messages.inter.MessageBase;

public abstract class ResponseCallback {

    public abstract void onResponse(MessageBase message);

    public void onErrorResponse(ErrorResponse message){
    }
}
