package unet.jtorrent.net.tunnel.messages;

import unet.jtorrent.net.tunnel.messages.inter.MessageBase;
import unet.jtorrent.net.tunnel.messages.inter.MessageType;

public class KeepAliveMessage extends MessageBase {

    public KeepAliveMessage(){
        type = MessageType.KEEP_ALIVE;
    }
}
