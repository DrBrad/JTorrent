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

    public static final byte[] PROTOCOL_HEADER = new byte[]{ 'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ', 'p', 'r', 'o', 't', 'o', 'c', 'o', 'l' };
    private TorrentManager manager;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public TCPSocket(TorrentManager manager){
        this.manager = manager;
    }

    @Override
    public void run(){
        try{
            handshake();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void connect(InetSocketAddress address)throws IOException {
        socket = new Socket();
        socket.connect(address);
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    public void handshake()throws IOException {
        // Create the handshake message
        //byte[] reservedBytes = new byte[8]; // Reserved bytes are typically all zeros
        //byte[] message = new byte[68]; // Handshake message is 68 bytes in length

        out.write(PROTOCOL_HEADER);
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
