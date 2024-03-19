package unet.jtorrent.net.tunnel.messages.inter;

public class MessageBase {

    protected MessageType type;

    public byte[] encode(){
        byte[] buf = new byte[type.getLength()+4];

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
}
