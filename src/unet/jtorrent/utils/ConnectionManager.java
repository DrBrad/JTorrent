package unet.jtorrent.utils;

import unet.jtorrent.net.peer.inter.ConnectionListener;
import unet.jtorrent.net.peer.inter.PeerSocket;
import unet.jtorrent.net.peer.tcp.TCPPeerSocket;

import java.util.ArrayList;
import java.util.List;

import static unet.jtorrent.TorrentClient.MAX_OPEN_CONNECTIONS;
import static unet.jtorrent.utils.Peer.MAX_STALE_COUNT;

public class ConnectionManager {

    private TorrentManager manager;
    private List<Peer> peers;
    private List<TCPPeerSocket> connected;

    public ConnectionManager(TorrentManager manager){
        this.manager = manager;
        peers = new ArrayList<>();
        connected = new ArrayList<>();
    }

    public void connectToPeer(){
        if(connected.size() >= MAX_OPEN_CONNECTIONS){
            return;
        }

        Peer peer = peers.get(0);
        peers.remove(peer);

        TCPPeerSocket socket = new TCPPeerSocket(manager, peer);
        connected.add(socket);
        socket.addConnectionListener(new ConnectionListener(){
            @Override
            public void onConnected(PeerSocket socket){
                peer.setSeen();
            }

            @Override
            public void onClosed(PeerSocket socket){
                peer.markStale();

                if(peer.getStale() < MAX_STALE_COUNT){
                    peers.add(peer);
                }

                connected.remove(socket); //NOT NEEDED...

                if(!peers.isEmpty()){
                    connectToPeer();
                }
            }
        });

        new Thread(socket).start();
    }

    public List<TCPPeerSocket> getConnections(){
        return connected;
    }

    public void addPeers(List<Peer> peers){
        this.peers.addAll(peers);
    }

    public void addPeer(Peer peer){
        peers.add(peer);
    }

    public int getTotalPotentialPeers(){
        return peers.size();
    }

    public int getTotalOpenConnections(){
        return connected.size();
    }
}
