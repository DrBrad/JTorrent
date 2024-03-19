package unet.jtorrent.net.tunnel.tcp;

import unet.jtorrent.TorrentClient;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.utils.TorrentManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPSocket implements Runnable {

    public static final String BITTORRENT_PROTOCOL_IDENTIFIER = "BitTorrent protocol";
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
    }

    public void handshake()throws IOException {
        // Create the handshake message
        //byte[] reservedBytes = new byte[8]; // Reserved bytes are typically all zeros
        //byte[] message = new byte[68]; // Handshake message is 68 bytes in length

        out.write((byte) BITTORRENT_PROTOCOL_IDENTIFIER.length());
        out.write(BITTORRENT_PROTOCOL_IDENTIFIER.getBytes("ISO-8859-1"));//PROTOCOL_HEADER);
        out.write(new byte[8]);
        out.write(manager.getTorrent().getInfo().getHash());
        out.write(manager.getClient().getPeerID());
        out.flush();

        // Send the handshake message
        //out.write(message);
        System.out.println("READING TIME");

        byte[] buf = new byte[4096];
        int len = in.read(buf);

        System.out.println(new String(buf, 0, len));

        out.flush();
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
}
