package unet.jtorrent.net.peer.inter;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.utils.Peer;
import unet.jtorrent.utils.TorrentManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class PeerSocket implements Runnable {

    protected TorrentManager manager;
    protected Peer peer;
    protected byte[] peerID;
    protected boolean[] pieces;
    protected List<ConnectionListener> listeners;

    public PeerSocket(TorrentManager manager, Peer peer){
        this.manager = manager;
        this.peer = peer;
        listeners = new ArrayList<>();
    }

    public abstract void send(MessageBase message)throws IOException;

    public void close(){
        if(!listeners.isEmpty()){
            for(ConnectionListener listener : listeners){
                listener.onClosed(this);
            }
        }
    }

    public boolean[] getPieces(){
        return pieces;
    }

    public boolean containsConnectionListener(ConnectionListener listener){
        return listeners.contains(listener);
    }

    public void addConnectionListener(ConnectionListener listener){
        listeners.add(listener);
    }

    public void removeConnectionListener(ConnectionListener listener){
        listeners.remove(listener);
    }
}
