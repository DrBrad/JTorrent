package unet.jtorrent.net.tunnel.tcp;

import unet.jtorrent.net.tunnel.inter.ConnectionListener;
import unet.jtorrent.net.tunnel.messages.KeepAliveMessage;
import unet.jtorrent.net.tunnel.messages.RequestMessage;
import unet.jtorrent.net.tunnel.messages.inter.MessageType;
import unet.jtorrent.utils.Peer;
import unet.jtorrent.utils.Piece;
import unet.jtorrent.utils.TorrentManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCPSocket implements Runnable {

    //public static final String BITTORRENT_PROTOCOL_IDENTIFIER = "BitTorrent protocol";
    public static final byte[] PROTOCOL_HEADER = new byte[]{ 'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ', 'p', 'r', 'o', 't', 'o', 'c', 'o', 'l' };
    private TorrentManager manager;
    private Peer peer;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private List<ConnectionListener> listeners;
    private byte[] peerID;
    private Piece piece;

    public TCPSocket(TorrentManager manager, Peer peer){
        this.manager = manager;
        this.peer = peer;
        listeners = new ArrayList<>();
    }

    @Override
    public void run(){
        try{
            socket = new Socket();
            socket.connect(peer.getAddress());
            if(!socket.isConnected()){
                close();
                return;
            }

            in = socket.getInputStream();
            out = socket.getOutputStream();

            handshake();

            if(!listeners.isEmpty()){
                for(ConnectionListener listener : listeners){
                    listener.onConnected(peer);
                }
            }


            /*
            socket.setKeepAlive(true);
            out.write(new KeepAliveMessage().encode());

            piece = manager.getDownloadManager().pollPiece();
            if(piece == null){
                close();
                return;
            }

            System.out.println("STARTING ON PIECE:  "+piece.getIndex()+"  CONNECTED: "+manager.getTotalOpenConnections()+"  PEERS: "+manager.getTotalPotentialPeers());

            //INTERESTED, REQUEST, OR PIECE
            RequestMessage message = new RequestMessage();
            message.setIndex(piece.getIndex());
            message.setBegin(piece.getOffset()); //WE COULD BEGIN BASED OFF OF WHERE WE LEFT OFF BUT THIS SEEMS LIKE IT WOULD BE INVALID ANYWAYS...
            message.setLength(manager.getTorrent().getInfo().getPieceLength());
            out.write(message.encode());

            //START READING THE PIECE

            byte[] buf = new byte[4096];
            in.read(buf);
            System.out.println(new String(buf));

            manager.getDownloadManager().completedPiece(piece);
            */

        }catch(IOException e){
            //e.printStackTrace();

        }finally{
            try{
                close();
            }catch(IOException e){
            }
        }
    }

    public void handshake()throws IOException {
        // Create the handshake message
        //byte[] reservedBytes = new byte[8]; // Reserved bytes are typically all zeros
        //byte[] message = new byte[68]; // Handshake message is 68 bytes in length

        /*
        +----------------------------------+--------------------------+------------------------+----------------------+
        | Protocol Header (20 bytes)       | Reserved Bytes (8 bytes) | Info Hash (20 bytes)   | Peer ID (20 bytes)   |
        +----------------------------------+--------------------------+------------------------+----------------------+
        | "BitTorrent protocol" (19 bytes) | 0x00 0x00 ... 0x00       | [Info Hash] (20 bytes) | [Peer ID] (20 bytes) |
        +----------------------------------+--------------------------+------------------------+----------------------+
        */

        out.write((byte) PROTOCOL_HEADER.length);
        out.write(PROTOCOL_HEADER);//PROTOCOL_HEADER);
        out.write(new byte[8]);
        out.write(manager.getTorrent().getInfo().getHash());
        out.write(manager.getClient().getPeerID());
        out.flush();

        // Send the handshake message
        //out.write(message);

        /*
        byte[] buf = new byte[4096];
        int len = in.read(buf);
        */

        if(in.read() != PROTOCOL_HEADER.length){
            throw new IOException("Protocol header is incorrect.");
        }

        byte[] protocolHeader = new byte[PROTOCOL_HEADER.length];
        in.read(protocolHeader);
        if(!Arrays.equals(protocolHeader, PROTOCOL_HEADER)){
            throw new IOException("Protocol header is incorrect \""+new String(protocolHeader, "ISO-8859-1")+"\"");
        }

        in.skip(8); //RESERVED SKIP

        byte[] infoHash = new byte[20];
        in.read(infoHash);

        if(!Arrays.equals(infoHash, manager.getTorrent().getInfo().getHash())){
            throw new IOException("Info Hash is incorrect.");
        }

        peerID = new byte[20];
        in.read(peerID);

        System.out.println(new String(protocolHeader, "ISO-8859-1")+"   "+bytesToHex(peerID)+"  "+manager.getTotalOpenConnections()+"  PEERS: "+manager.getTotalPotentialPeers());
    }

    public void close()throws IOException {
        if(piece != null){
            manager.getDownloadManager().failedPiece(piece);
        }

        if(!listeners.isEmpty()){
            for(ConnectionListener listener : listeners){
                listener.onClosed(peer);
            }
        }

        if(!socket.isClosed()){
            if(!socket.isInputShutdown()){
                socket.shutdownInput();
            }

            if(!socket.isOutputShutdown()){
                socket.shutdownOutput();
            }

            socket.close();
        }
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

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
