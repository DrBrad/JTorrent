package unet.jtorrent.net.trackers.udp.messages;

import unet.jtorrent.net.trackers.udp.messages.inter.MessageAction;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;
import unet.kad4.utils.ByteWrapper;

import java.util.ArrayList;
import java.util.List;

public class ScrapeRequest extends MessageBase {

    /*
    Offset          Size            Name            Value
    0               64-bit integer  connection_id
    8               32-bit integer  action          2 // scrape
    12              32-bit integer  transaction_id
    16 + 20 * n     20-byte string  info_hash
    16 + 20 * N
    * */
    private long connectionID;
    private List<ByteWrapper> infoHashes;

    public ScrapeRequest(){
        action = MessageAction.SCRAPE;
        infoHashes = new ArrayList<>();
    }

    public ScrapeRequest(byte[] tid){
        super(tid);
        action = MessageAction.SCRAPE;
        infoHashes = new ArrayList<>();
    }

    @Override
    public byte[] encode(){
        return new byte[0];
    }

    @Override
    public void decode(byte[] buf, int off, int len){
        buf[0] = ((byte) (connectionID >> 56));
        buf[1] = ((byte) (connectionID >> 48));
        buf[2] = ((byte) (connectionID >> 40));
        buf[3] = ((byte) (connectionID >> 32));
        buf[4] = ((byte) (connectionID >> 24));
        buf[5] = ((byte) (connectionID >> 16));
        buf[6] = ((byte) (connectionID >>  8));
        buf[7] = ((byte) connectionID);

        buf[8] = ((byte) (action.getCode() >> 24));
        buf[9] = ((byte) (action.getCode() >> 16));
        buf[10] = ((byte) (action.getCode() >> 8));
        buf[11] = ((byte) action.getCode());

        System.arraycopy(tid, 0, buf, 12, tid.length);

        for(int i = 0; i < infoHashes.size(); i++){
            System.arraycopy(infoHashes.get(i).getBytes(), 0, buf, 16+(i*20), infoHashes.get(i).getBytes().length);
        }
    }

    public void setConnectionID(long connectionID){
        this.connectionID = connectionID;
    }

    public long getConnectionID(){
        return connectionID;
    }

    public boolean containsInfoHash(byte[] infoHash){
        return infoHashes.contains(new ByteWrapper(infoHash));
    }

    public void addInfoHash(byte[] infoHash){
        if(infoHash.length != 20){
            throw new IllegalArgumentException("Info hash is not correct length");
        }

        infoHashes.add(new ByteWrapper(infoHash));
    }

    public void removeInfoHash(byte[] infoHash){
        infoHashes.remove(new ByteWrapper(infoHash));
    }

    public List<byte[]> getAllInfoHashes(){
        List<byte[]> r = new ArrayList<>();

        for(ByteWrapper b : infoHashes){
            r.add(b.getBytes());
        }

        return r;
    }

    public int getInfoHashesSize(){
        return infoHashes.size();
    }
}
