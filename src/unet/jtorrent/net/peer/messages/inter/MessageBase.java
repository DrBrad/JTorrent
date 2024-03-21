package unet.jtorrent.net.peer.messages.inter;

public class MessageBase {

    protected MessageType type;

    public byte[] encode(){
        byte[] buf = new byte[getLength()];

        buf[0] = ((byte) (type.getLength() >> 24));
        buf[1] = ((byte) (type.getLength() >> 16));
        buf[2] = ((byte) (type.getLength() >> 8));
        buf[3] = ((byte) type.getLength());

        if(type.getLength() > 0){
            buf[4] = (type.getID());
        }

        return buf;
    }

    public void decode(byte[] buf){

    }

    public MessageType getType(){
        return type;
    }

    public int getLength(){
        return type.getLength()+4;
    }
}
