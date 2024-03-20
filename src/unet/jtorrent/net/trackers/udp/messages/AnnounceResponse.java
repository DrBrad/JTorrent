package unet.jtorrent.net.trackers.udp.messages;

import unet.jtorrent.net.trackers.udp.messages.inter.MessageAction;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;
import unet.jtorrent.utils.Peer;
import unet.jtorrent.utils.PeerUtils;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class AnnounceResponse extends MessageBase {

    /*
    Offset      Size            Name            Value
    0           32-bit integer  action          1 // announce
    4           32-bit integer  transaction_id
    8           32-bit integer  interval
    12          32-bit integer  leechers
    16          32-bit integer  seeders
    20 + 6 * n  32-bit integer  IP address
    24 + 6 * n  16-bit integer  TCP port
    20 + 6 * N
    * */

    private int interval, leechers, seeders;
    private List<Peer> peers;

    public AnnounceResponse(byte[] tid){
        super(tid);
        action = MessageAction.ANNOUNCE;
        peers = new ArrayList<>();
    }

    @Override
    public byte[] encode(){
        return new byte[0];
    }

    @Override
    public void decode(byte[] buf, int off, int len){
        interval = (((buf[off] & 0xff) << 24) |
                ((buf[off+1] & 0xff) << 16) |
                ((buf[off+2] & 0xff) << 8) |
                (buf[off+3] & 0xff));

        leechers = (((buf[off+4] & 0xff) << 24) |
                ((buf[off+5] & 0xff) << 16) |
                ((buf[off+6] & 0xff) << 8) |
                (buf[off+7] & 0xff));

        seeders = (((buf[off+8] & 0xff) << 24) |
                ((buf[off+9] & 0xff) << 16) |
                ((buf[off+10] & 0xff) << 8) |
                (buf[off+11] & 0xff));

        byte[] addr;
        if(origin.getAddress() instanceof Inet4Address){
            addr = new byte[6];

        }else{
            addr = new byte[18];
        }

        int position = off+12;
        while(position < len){
            System.arraycopy(buf, position, addr, 0, addr.length);
            peers.add(PeerUtils.unpackAddress(addr));
            position += addr.length;
        }
    }

    public void setInterval(int interval){
        this.interval = interval;
    }

    public int getInterval(){
        return interval;
    }

    public void setLeechers(int leechers){
        this.leechers = leechers;
    }

    public int getLeechers(){
        return leechers;
    }

    public void setSeeders(int seeders){
        this.seeders = seeders;
    }

    public int getSeeders(){
        return seeders;
    }

    public boolean containsPeer(Peer peer){
        return peers.contains(peer);
    }

    public void addPeer(Peer peer){
        peers.add(peer);
    }

    public void removePeer(Peer peer){
        peers.remove(peer);
    }

    public List<Peer> getAllPeers(){
        return peers;
    }

    public int getTotalPeers(){
        return peers.size();
    }
}
