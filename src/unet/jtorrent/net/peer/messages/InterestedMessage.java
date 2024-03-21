package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class InterestedMessage extends MessageBase {

    public InterestedMessage(){
        type = MessageType.INTERESTED;
    }
}
