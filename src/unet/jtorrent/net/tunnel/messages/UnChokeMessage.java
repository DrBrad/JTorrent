package unet.jtorrent.net.tunnel.messages;

import unet.jtorrent.net.tunnel.messages.inter.MessageBase;
import unet.jtorrent.net.tunnel.messages.inter.MessageType;

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
