package unet.jtorrent.net.tunnel.messages;

import unet.jtorrent.net.tunnel.messages.inter.MessageBase;
import unet.jtorrent.net.tunnel.messages.inter.MessageType;

public class InterestedMessage extends MessageBase {

    public InterestedMessage(){
        type = MessageType.INTERESTED;
    }
}
