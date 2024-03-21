package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class PieceMessage extends MessageBase {

    private int index, begin;
    private byte[] block;

    public PieceMessage(){
        type = MessageType.PIECE;
    }

    @Override
    public byte[] encode(){
        byte[] buf = super.encode();

        buf[5] = ((byte) (index >> 24));
        buf[6] = ((byte) (index >> 16));
        buf[7] = ((byte) (index >> 8));
        buf[8] = ((byte) index);

        buf[9] = ((byte) (begin >> 24));
        buf[10] = ((byte) (begin >> 16));
        buf[11] = ((byte) (begin >> 8));
        buf[12] = ((byte) begin);

        block = new byte[buf.length-12];
        System.arraycopy(buf, 13, block, 0, block.length);

        return buf;
    }

    @Override
    public void decode(byte[] buf){
        super.decode(buf);

        index = (((buf[0] & 0xff) << 24) |
                ((buf[1] & 0xff) << 16) |
                ((buf[2] & 0xff) << 8) |
                (buf[3] & 0xff));

        begin = (((buf[4] & 0xff) << 24) |
                ((buf[5] & 0xff) << 16) |
                ((buf[6] & 0xff) << 8) |
                (buf[7] & 0xff));

        block = new byte[buf.length-8];
        System.arraycopy(buf, 8, block, 0, block.length);
    }

    @Override
    public int getLength(){
        return super.getLength()+block.length;
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

    public void setBlock(byte[] block){
        this.block = block;
    }

    public byte[] getBlock(){
        return block;
    }
}
