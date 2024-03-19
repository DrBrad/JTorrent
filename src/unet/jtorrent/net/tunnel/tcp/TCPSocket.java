package unet.jtorrent.net.tunnel.tcp;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.utils.TorrentManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class TCPSocket implements Runnable {

    //public static final String BITTORRENT_PROTOCOL_IDENTIFIER = "BitTorrent protocol";
    public static final byte[] PROTOCOL_HEADER = new byte[]{ 'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ', 'p', 'r', 'o', 't', 'o', 'c', 'o', 'l' };
    private TorrentManager manager;
    private InetSocketAddress address;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public TCPSocket(TorrentManager manager, InetSocketAddress address){
        this.manager = manager;
        this.address = address;
    }

    @Override
    public void run(){
        try{
            connect();
        }catch(IOException e){
            e.printStackTrace();

        }finally{
            try{
                close();
            }catch(IOException e){
            }
        }
    }

    public void connect()throws IOException {
        socket = new Socket();
        socket.connect(address);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        handshake();

        socket.setKeepAlive(true);
        sendMessage(PeerMessage.KEEP_ALIVE);

        //INTERESTED, REQUEST, OR PIECE
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
            System.err.println("PROTOCOL HEADER LENGTH ISNT CORRECT");
            return; //CLOSE
        }

        byte[] protocolHeader = new byte[PROTOCOL_HEADER.length];
        in.read(protocolHeader);
        if(!Arrays.equals(protocolHeader, PROTOCOL_HEADER)){
            System.err.println("PROTOCOL HEADER ISNT CORRECT");
            return; //CLOSE
        }

        in.skip(8); //RESERVED SKIP

        byte[] infoHash = new byte[20];
        in.read(infoHash);

        if(!Arrays.equals(infoHash, manager.getTorrent().getInfo().getHash())){
            System.err.println("INFO_HASH ISNT CORRECT");
            return; //CLOSE
        }



        byte[] peerID = new byte[20];
        in.read(peerID);

        //SENDERS PEER_ID




        System.out.println(new String(protocolHeader, "ISO-8859-1")+"   "+bytesToHex(peerID));


        //sendMessage(PeerMessage.KEEP_ALIVE);



        //out.flush();
    }

    public void sendMessage(PeerMessage message)throws IOException {
        out.write(new byte[]{
                (byte) (message.getLength() >> 24),
                (byte) (message.getLength() >> 16),
                (byte) (message.getLength() >> 8),
                (byte) message.getLength()
        });

        if(message.getLength() > 0){
            out.write(message.getID());
        }

        //out.flush();
    }

    public void close()throws IOException {
        if(!socket.isClosed()){
            if(!socket.isInputShutdown()){
                in.close();
            }

            if(!socket.isOutputShutdown()){
                out.close();
            }

            socket.close();
        }
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
