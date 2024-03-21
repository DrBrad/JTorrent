package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class KeepAliveMessage extends MessageBase {

    public KeepAliveMessage(){
        type = MessageType.KEEP_ALIVE;
    }
}
