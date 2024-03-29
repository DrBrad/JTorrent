package unet.jtorrent.net.trackers.udp.messages;

import unet.jtorrent.net.trackers.udp.messages.inter.MessageAction;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;

public class ErrorResponse extends MessageBase {

    /*
    Offset  Size            Name            Value
    0       32-bit integer  action          3 // error
    4       32-bit integer  transaction_id
    8       string  message
    * */

    private String message;

    public ErrorResponse(byte[] tid){
        this.tid = tid;
        action = MessageAction.ERROR;
    }

    @Override
    public byte[] encode(){
        byte[] message = this.message.getBytes();
        byte[] buf = new byte[message.length+8];

        buf[0] = ((byte) action.getCode());
        buf[1] = ((byte) (action.getCode() >> 8));
        buf[2] = ((byte) (action.getCode() >> 16));
        buf[3] = ((byte) (action.getCode() >> 24));

        System.arraycopy(tid, 0, buf, 4, tid.length);

        System.arraycopy(message, 0, buf, 8, message.length);

        return buf;
    }

    @Override
    public void decode(byte[] buf, int off, int len){
        message = new String(buf, off, len);
        System.out.println(message);
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
