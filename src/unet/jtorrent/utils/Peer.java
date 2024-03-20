package unet.jtorrent.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Peer {

    private InetSocketAddress address;
    private int stale;

    public Peer(InetSocketAddress address){
        this.address = address;
    }

    public InetSocketAddress getAddress(){
        return address;
    }

    public InetAddress getHostAddress(){
        return address.getAddress();
    }

    public int getPort(){
        return address.getPort();
    }

    public void markStale(){
        stale++;
    }

    public int getStale(){
        return stale;
    }

    public void setSeen(){
        stale = 0;
    }

    @Override
    public int hashCode(){
        return address.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Peer){
            return hashCode() == o.hashCode();
            //return address.equals(((Node) o).address) && port == ((Node) o).port;
        }
        return false;
    }
}
