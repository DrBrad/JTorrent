package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class UnChokeMessage extends MessageBase {

    public UnChokeMessage(){
        type = MessageType.UNCHOKE;
    }
}
