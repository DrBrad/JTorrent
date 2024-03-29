package unet.jtorrent.net.trackers.udp.messages;

import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageAction;

public class ConnectResponse extends MessageBase {

    /*
    Offset  Size            Name            Value
    0       32-bit integer  action          0 // connect
    4       32-bit integer  transaction_id
    8       64-bit integer  connection_id
    16
    */

    private long connectionID;

    public ConnectResponse(byte[] tid){
        super(tid);
        action = MessageAction.CONNECT;
    }

    @Override
    public byte[] encode(){
        return new byte[0];
    }

    @Override
    public void decode(byte[] buf, int off, int len){
        /*
        connectionID = (((long) (buf[off] & 0xff)) |
                ((long) (buf[off+1] & 0xff) <<  8) |
                ((long) (buf[off+2] & 0xff) << 16) |
                ((long) (buf[off+3] & 0xff) << 24) |
                ((long) (buf[off+4] & 0xff) << 32) |
                ((long) (buf[off+5] & 0xff) << 40) |
                ((long) (buf[off+6] & 0xff) << 48) |
                ((long) (buf[off+7] & 0xff) << 56));
        */
        connectionID = (((long) (buf[off] & 0xff) << 56) |
                ((long) (buf[off+1] & 0xff) << 48) |
                ((long) (buf[off+2] & 0xff) << 40) |
                ((long) (buf[off+3] & 0xff) << 32) |
                ((long) (buf[off+4] & 0xff) << 24) |
                ((long) (buf[off+5] & 0xff) << 16) |
                ((long) (buf[off+6] & 0xff) <<  8) |
                ((long) (buf[off+7] & 0xff)));
    }

    public long getConnectionID(){
        return connectionID;
    }

    public void setConnectionID(long connectionID){
        this.connectionID = connectionID;
    }
}
