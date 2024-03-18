package unet.jtorrent.trackers.udp.messages;

import unet.jtorrent.trackers.udp.messages.inter.MessageAction;
import unet.jtorrent.trackers.udp.messages.inter.MessageBase;

public class ScrapeResponse extends MessageBase {

    public ScrapeResponse(byte[] tid){
        super(tid);
        action = MessageAction.SCRAPE;
    }

    @Override
    public byte[] encode(){
        return new byte[0];
    }

    @Override
    public void decode(byte[] buf, int off, int len){

    }
}
