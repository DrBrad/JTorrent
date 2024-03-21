package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class HaveMessage extends MessageBase {

    private int index;

    public HaveMessage(){
        type = MessageType.HAVE;
    }

    @Override
    public byte[] encode(){
        byte[] buf = super.encode();

        buf[5] = ((byte) (index >> 24));
        buf[6] = ((byte) (index >> 16));
        buf[7] = ((byte) (index >> 8));
        buf[8] = ((byte) index);

        return buf;
    }

    @Override
    public void decode(byte[] buf){
        super.decode(buf);

        index = (((buf[0] & 0xff) << 24) |
                ((buf[1] & 0xff) << 16) |
                ((buf[2] & 0xff) << 8) |
                (buf[3] & 0xff));
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }
}
