package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class RequestMessage extends MessageBase {

    private int index, begin = 0, length;

    public RequestMessage(){
        type = MessageType.REQUEST;
    }

    @Override
    public byte[] encode(){
        byte[] buf = super.encode();

        buf[6] = ((byte) (index >> 24));
        buf[7] = ((byte) (index >> 16));
        buf[8] = ((byte) (index >> 8));
        buf[9] = ((byte) index);

        buf[10] = ((byte) (begin >> 24));
        buf[11] = ((byte) (begin >> 16));
        buf[12] = ((byte) (begin >> 8));
        buf[13] = ((byte) begin);

        buf[14] = ((byte) (length >> 24));
        buf[15] = ((byte) (length >> 16));
        buf[16] = ((byte) (length >> 8));
        buf[17] = ((byte) length);

        return buf;
    }

    @Override
    public void decode(byte[] buf){
        super.decode(buf);
    }

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    public void setBegin(int begin){
        this.begin = begin;
    }

    public int getBegin(){
        return begin;
    }

    public void setLength(int length){
        this.length = length;
    }

    public int getLength(){
        return length;
    }
}
