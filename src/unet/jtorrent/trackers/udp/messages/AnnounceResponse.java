package unet.jtorrent.trackers.udp.messages;

import unet.jtorrent.trackers.udp.messages.inter.MessageAction;
import unet.jtorrent.trackers.udp.messages.inter.MessageBase;
import unet.jtorrent.utils.PeerUtils;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class AnnounceResponse extends MessageBase {

    private int interval, leachers, seeders;
    private List<InetSocketAddress> peers;

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

        leachers = (((buf[off+4] & 0xff) << 24) |
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

    public void setLeachers(int leachers){
        this.leachers = leachers;
    }

    public int getLeachers(){
        return leachers;
    }

    public void setSeeders(int seeders){
        this.seeders = seeders;
    }

    public int getSeeders(){
        return seeders;
    }

    public boolean containsPeer(InetSocketAddress address){
        return peers.contains(address);
    }

    public void addPeer(InetSocketAddress address){
        peers.add(address);
    }

    public void removePeer(InetSocketAddress address){
        peers.remove(address);
    }

    public List<InetSocketAddress> getAllPeers(){
        return peers;
    }
}
