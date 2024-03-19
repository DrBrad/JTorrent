package unet.jtorrent.net.trackers.udp.messages;

import unet.jtorrent.net.trackers.udp.messages.inter.MessageAction;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;

public class ScrapeResponse extends MessageBase {

    /*
    Offset      Size            Name            Value
    0           32-bit integer  action          2 // scrape
    4           32-bit integer  transaction_id
    8 + 12 * n  32-bit integer  seeders
    12 + 12 * n 32-bit integer  completed
    16 + 12 * n 32-bit integer  leechers
    8 + 12 * N
    * */
    private int count;
    private int[] seeders, completed, leechers;

    public ScrapeResponse(byte[] tid){
        super(tid);
        action = MessageAction.SCRAPE;
    }

    public ScrapeResponse(byte[] tid, int count){
        super(tid);
        action = MessageAction.SCRAPE;
        this.count = count;
    }

    @Override
    public byte[] encode(){
        return new byte[0];
    }

    @Override
    public void decode(byte[] buf, int off, int len){
        seeders = new int[count];
        completed = new int[count];
        leechers = new int[count];

        for(int i = 0; i < count; i++){
            seeders[i] = (((buf[off+(i*4)] & 0xff) << 24) |
                    ((buf[off+(i*4)+1] & 0xff) << 16) |
                    ((buf[off+(i*4)+2] & 0xff) << 8) |
                    (buf[off+(i*4)+3] & 0xff));
        }

        for(int i = count; i < count*2; i++){
            completed[i] = (((buf[off+(i*4)] & 0xff) << 24) |
                    ((buf[off+(i*4)+1] & 0xff) << 16) |
                    ((buf[off+(i*4)+2] & 0xff) << 8) |
                    (buf[off+(i*4)+3] & 0xff));
        }

        for(int i = count; i < count*3; i++){
            leechers[i] = (((buf[off+(i*4)] & 0xff) << 24) |
                    ((buf[off+(i*4)+1] & 0xff) << 16) |
                    ((buf[off+(i*4)+2] & 0xff) << 8) |
                    (buf[off+(i*4)+3] & 0xff));
        }
    }

    public void setInfoHashCount(int count){
        this.count = count;
    }

    public void setSeeders(int i, int seeders){
        this.seeders[i] = seeders;
    }

    public int getSeeders(int i){
        return seeders[i];
    }

    public int[] getAllSeeders(){
        return seeders;
    }

    public void setCompleted(int i, int completed){
        this.completed[i] = completed;
    }

    public int getCompleted(int i){
        return completed[i];
    }

    public int[] getAllCompleted(){
        return completed;
    }

    public void setLeechers(int i, int leechers){
        this.leechers[i] = leechers;
    }

    public int getLeechers(int i){
        return leechers[i];
    }

    public int[] getAllLeechers(){
        return leechers;
    }
}
