package unet.jtorrent.net.tunnel.messages;

import unet.jtorrent.net.tunnel.messages.inter.MessageBase;
import unet.jtorrent.net.tunnel.messages.inter.MessageType;

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
