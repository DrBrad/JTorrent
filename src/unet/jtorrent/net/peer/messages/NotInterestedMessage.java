package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class NotInterestedMessage extends MessageBase {

    public NotInterestedMessage(){
        type = MessageType.NOT_INTERESTED;
    }
}
