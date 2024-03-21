package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class UnChokeMessage extends MessageBase {

    public UnChokeMessage(){
        type = MessageType.UNCHOKE;
    }

    @Override
    public byte[] encode(){
        return super.encode();
    }

    @Override
    public void decode(byte[] buf){
    }
}
