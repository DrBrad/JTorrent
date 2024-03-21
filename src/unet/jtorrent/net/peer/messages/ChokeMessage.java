package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class ChokeMessage extends MessageBase {

    public ChokeMessage(){
        type = MessageType.CHOKE;
    }

    @Override
    public byte[] encode(){
        return super.encode();
    }

    @Override
    public void decode(byte[] buf){
    }
}
