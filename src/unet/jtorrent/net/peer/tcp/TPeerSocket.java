package unet.jtorrent.net.peer.tcp;

import unet.jtorrent.net.peer.inter.ConnectionListener;
import unet.jtorrent.net.peer.inter.PeerSocket;
import unet.jtorrent.net.peer.messages.*;
import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;
import unet.jtorrent.utils.*;
import unet.jtorrent.utils.inter.PieceState;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import static unet.jtorrent.utils.DownloadManager.BLOCK_SIZE;

public class TPeerSocket extends PeerSocket {

    //public static final String BITTORRENT_PROTOCOL_IDENTIFIER = "BitTorrent protocol";
    public static final byte[] PROTOCOL_HEADER = new byte[]{ 'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ', 'p', 'r', 'o', 't', 'o', 'c', 'o', 'l' };
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private boolean bitfield, choked, interested, requesting;

    public TPeerSocket(TorrentManager manager, Peer peer){
        super(manager, peer);
    }

    @Override
    public void run(){
        try{
            socket = new Socket();
            socket.connect(peer.getAddress(), 5000);
            //socket.setSoTimeout(5000);
            //if(!socket.isConnected()){
            //    close();
            //    return;
            //}

            in = socket.getInputStream();
            out = socket.getOutputStream();

            if(!listeners.isEmpty()){
                for(ConnectionListener listener : listeners){
                    listener.onConnected(this);
                }
            }

            handshake();


            //SEND BIT-FIELD
            if(manager.getDownloadManager().getTotalCompleted() > 0){
                BitfieldMessage message = new BitfieldMessage(); //USE DOWNLOAD MANAGER FOR THIS...
                message.setNumPieces(manager.getTorrent().getInfo().getTotalPieces());

                for(Piece piece : manager.getTorrent().getInfo().getPieces()){
                    if(piece.getState() == PieceState.COMPLETE){
                        message.setPiece(piece.getIndex(), true);
                    }
                }

                out.write(message.encode());
                out.flush();
            }

            receive();

            //SEND INTERESTED
            if(manager.getDownloadManager().isInterested(pieces)){
                out.write(new InterestedMessage().encode());
                out.flush();
            }

            if(!listeners.isEmpty()){
                for(ConnectionListener listener : listeners){
                    listener.onReadyToSend(this);
                }
            }

            //RECEIVER - KEEP ALIVE EVERY 5 SECONDS...
            while(!socket.isClosed()){
                receive();

                /*
                if(in.available() < 1){
                    out.write(new KeepAliveMessage().encode());
                    out.flush();
                }
                */
            }

            System.err.println("CLOSE NAtURAL");
            /*
            new Thread(new Runnable(){
                @Override
                public void run(){
                }
            }).start();

            //KeepAliveMessage message = new KeepAliveMessage();
            //out.write(message.encode());
            //out.flush();
            /*
            */

            /*
            out.write(new InterestedMessage().encode());
            out.flush();

                //System.out.println("REQUESTING: "+piece.getIndex());

            while (in.available() > 0){
                receive();
            }
            //System.err.println("SENDING REQUEST FOR: "+mcount);

            piece = manager.getDownloadManager().startPiece(pieces);
            if(pieces != null){
                RequestMessage message = new RequestMessage();
                message.setIndex(piece.getIndex());
                message.setBegin(piece.getOffset()); //WE COULD BEGIN BASED OFF OF WHERE WE LEFT OFF BUT THIS SEEMS LIKE IT WOULD BE INVALID ANYWAYS...
                message.setLength(BLOCK_SIZE);
                out.write(message.encode());
                out.flush();

                test = true;
                while (in.available() > 0){
                    receive();
                }
            }

                /*
                byte[] buf = new byte[BLOCK_SIZE];
                int len = in.read(buf);

            int length = (((buf[0] & 0xff) << 24) |
                    ((buf[1] & 0xff) << 16) |
                    ((buf[2] & 0xff) << 8) |
                    (buf[3] & 0xff));

            MessageType type;

            if(length > 0){
                type = MessageType.getFromID(buf[4]);
            }else{
                type = MessageType.KEEP_ALIVE;
            }
            System.out.println("READ: "+type+"  "+len);
                /*
            int length = (((buf[0] & 0xff) << 24) |
                    ((buf[1] & 0xff) << 16) |
                    ((buf[2] & 0xff) << 8) |
                    (buf[3] & 0xff));

            MessageType type;

            if(length > 0){
                type = MessageType.getFromID(buf[4]);
            }else{
                type = MessageType.KEEP_ALIVE;
            }
            System.out.println("READ: "+type+"  "+len);



                //TIMEOUT AFTER 5 SECONDS...
                //DETERMINE WHERE THE PIECE SHOULD GO...

                /*
            for(int i = 0; i < 5; i++){
                while(in.available() < 1){
                }

                byte[] buf = new byte[BLOCK_SIZE];
                int len = in.read(buf);
                //receive();


                int length = (((buf[0] & 0xff) << 24) |
                        ((buf[1] & 0xff) << 16) |
                        ((buf[2] & 0xff) << 8) |
                        (buf[3] & 0xff));

                MessageType type;

                if(length > 0){
                    type = MessageType.getFromID(buf[4]);
                }else{
                    type = MessageType.KEEP_ALIVE;
                }
                System.out.println("READ: "+type+"  "+len);
            }

                /*

                TorrentFile torrentFile = null;
                long offset = piece.getOffset();
                for(TorrentFile f : manager.getTorrent().getInfo().getFiles()){
                    if((long) piece.getIndex()*manager.getTorrent().getInfo().getPieceLength() < offset+f.getLength()){
                        torrentFile = f;
                        break;
                    }
                    offset += f.getLength();
                }

                RandomAccessFile file = new RandomAccessFile(new File(manager.getDownloadManager().getDestination(), torrentFile.getPathString()), "rw");
                long pos = (int) ((piece.getIndex()*manager.getTorrent().getInfo().getPieceLength())-offset);
                file.seek(pos);

                byte[] buf = new byte[BLOCK_SIZE]; //
                //TorrentFile torrentFile = manager.getTorrent().getInfo().getFileFromPiece(piece.getIndex());
                int len, read = 0;
                //while(read < manager.getTorrent().getInfo().getPieceLength()){
                //    len = in.read(buf);
                while((len = in.read(buf)) != -1){
                    if(pos+len > torrentFile.getLength()){
                        file.write(buf, 0, (int) (torrentFile.getLength()-pos));
                        //WRITE THE LAST BYTES
                        torrentFile = manager.getTorrent().getInfo().getFile(torrentFile.getIndex()+1);
                        pos = len-(torrentFile.getLength()-pos);
                        file = new RandomAccessFile(new File(manager.getDownloadManager().getDestination(), torrentFile.getPathString()), "rw");
                        file.write(buf, 0, (int) pos);
                    }

                    out.write(buf);
                    pos += len;

                    read += len;
                    System.out.println("WRITING: "+len+"  - "+piece.getIndex());
                    //DETERMINE FILE... - (index*PIECE_LENGTH) - FILE INDEX STARTS 0+ THEN WE MAY NEED TO SPLIT BLOCK IF BETWEEN FILES
                }

                System.err.println("COMPLETED  "+read);*/
                //System.out.println("AVAILABLE: "+t.length);
            //}

            //READ ID CODE - WHAT THEY SENT

            //while(in.available() > 0){
            //}

            //KEEP ALIVE



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
            //close();
            if(!listeners.isEmpty()){
                for(ConnectionListener listener : listeners){
                    listener.onClosed(this);
                }
            }
        }
    }

    private void handshake()throws IOException {
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

        //System.out.println(new String(protocolHeader, "ISO-8859-1")+"   "+bytesToHex(peerID)+"  "+manager.getTotalOpenConnections()+"  PEERS: "+manager.getTotalPotentialPeers());
    }

    private void receive()throws IOException {
        int length = (((in.read() & 0xff) << 24) |
                ((in.read() & 0xff) << 16) |
                ((in.read() & 0xff) << 8) |
                (in.read() & 0xff));

        if(length > 0){
            byte id = (byte) in.read();

            MessageType type = MessageType.getFromID(id);

            //System.out.println(type+"   "+length+"  "+bitfield);

            MessageBase message;

            switch(type){
                case CHOKE:
                    choked = true;
                    break;

                case UNCHOKE:
                    choked = false;
                    break;

                case INTERESTED:
                    interested = true;
                    break;

                case NOT_INTERESTED:
                    interested = false;
                    break;

                case HAVE: {
                        //MODIFY BITFIELD WITH NEW CHANGES...
                        message = new HaveMessage(); //ONLY ALLOWED AFTER HANDSHAKE...
                        byte[] buf = new byte[length-1];
                        in.read(buf);
                        message.decode(buf);
                        pieces[((HaveMessage) message).getIndex()] = true;
                        System.out.println("MESSAGE - HAVE: "+((HaveMessage) message).getIndex()+"       "+peer.getHostAddress().getHostAddress());
                    }
                    return;//MAKE break;

                case BITFIELD: {
                        if(bitfield){
                            throw new IOException("Peer sent bitfield more than once.");
                        }

                        if(length-1 != (int) Math.ceil(manager.getTorrent().getInfo().getTotalPieces()/8.0)){
                            throw new IOException("Bitfield is incorrect size");
                        }

                        message = new BitfieldMessage(); //ONLY ALLOWED AFTER HANDSHAKE...
                        ((BitfieldMessage) message).setNumPieces(manager.getTorrent().getInfo().getTotalPieces());
                        byte[] buf = new byte[length-1];
                        in.read(buf);
                        message.decode(buf);
                        pieces = ((BitfieldMessage) message).getPieces();
                        bitfield = true;
                        System.out.println("BITFIELD: "+peer.getHostAddress().getHostAddress()+" "+message);
                    }
                    return;

                case REQUEST: {
                        byte[] buf = new byte[length-1];
                        in.read(buf);

                        if(!interested){
                            //OTHERWISE IGNORE...
                            return;
                        }

                        message = new RequestMessage(); //ONLY ALLOWED AFTER HANDSHAKE...
                        message.decode(buf);

                    }
                    //System.out.println("REQUEST");
                    break;

                case PIECE: {
                        byte[] buf = new byte[length-1];
                        in.read(buf);

                        message = new PieceMessage();
                        message.decode(buf);

                        //System.err.println("READING PIECE START   "+peer.getHostAddress().getHostAddress());

                        //((PieceMessage) message).getBegin();

                        //SAVE PIECE...

                    try{

                        TorrentFile torrentFile = null;
                        long previousLength = 0;
                        long offset = (((PieceMessage) message).getIndex()*manager.getTorrent().getInfo().getPieceLength())+((PieceMessage) message).getBegin();

                        for(TorrentFile f : manager.getTorrent().getInfo().getFiles()){
                            if(offset < previousLength+f.getLength()){
                                torrentFile = f;
                                break;
                            }

                            previousLength += f.getLength();
                        }

                        //EXAMPLE - FILE 3 IS THE QUERY - 40 bytes in
                        //FILE 1 & 2 are both 1000 bytes
                        //offset would 2040
                        //previous-length would be 2000
                        //-------------
                        //calculated file offset is 40
                        offset = offset-previousLength;

                        RandomAccessFile file = new RandomAccessFile(new File(manager.getDownloadManager().getDestination(), torrentFile.getPathString()), "rw");
                        file.seek(offset);

                        file.write(((PieceMessage) message).getBlock(), 0, ((PieceMessage) message).getBlock().length);
                        offset += ((PieceMessage) message).getBlock().length;

                        byte[] block = new byte[BLOCK_SIZE];
                        int len;
                        int read = 0;

                        while(read < manager.getTorrent().getInfo().getPieceLength()){
                            len = in.read(block);
                            read += len;

                            if(offset+len > torrentFile.getLength()){
                                int off = (int) (torrentFile.getLength()-offset);
                                file.write(block, 0, off);
                                file.close();

                                //START ON NEXT FILE
                                torrentFile = manager.getTorrent().getInfo().getFile(torrentFile.getIndex()+1);
                                file = new RandomAccessFile(new File(manager.getDownloadManager().getDestination(), torrentFile.getPathString()), "rw");
                                file.write(block, off, len-off);

                                offset += len;
                                System.err.println("STARTING NEW FILE - "+torrentFile.getIndex()+" READ "+read);

                                continue;
                            }

                            file.write(block, 0, len);
                            System.err.println("WRITING - "+torrentFile.getIndex()+" - "+len+" - "+peer.getHostAddress().getHostAddress()+"  OFFSET "+offset+" PIECE-LENGTH "+manager.getTorrent().getInfo().getPieceLength()+" READ "+read);
                            offset += len;
                        }

                        file.close();

                        System.err.println("========================================= COMPLETED TORRENT =========================================");
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        /*
                        Piece piece = manager.getTorrent().getInfo().getPiece(((PieceMessage) message).getIndex());

                        TorrentFile torrentFile = null;
                        //long offset = piece.getOffset();
                        long offset = ((PieceMessage) message).getBegin();
                        for(TorrentFile f : manager.getTorrent().getInfo().getFiles()){
                            if((long) piece.getIndex()*manager.getTorrent().getInfo().getPieceLength() < offset+f.getLength()){
                                torrentFile = f;
                                break;
                            }
                            offset += f.getLength();
                        }

                        RandomAccessFile file = new RandomAccessFile(new File(manager.getDownloadManager().getDestination(), torrentFile.getPathString()), "rw");
                        long pos = (int) ((piece.getIndex()*manager.getTorrent().getInfo().getPieceLength())-offset);
                        file.seek(pos);



                        if(pos+((PieceMessage) message).getBlock().length > torrentFile.getLength()){
                            file.write(buf, 0, (int) (torrentFile.getLength()-pos));
                            //WRITE THE LAST BYTES
                            torrentFile = manager.getTorrent().getInfo().getFile(torrentFile.getIndex()+1);
                            pos = ((PieceMessage) message).getBlock().length-(torrentFile.getLength()-pos);
                            file = new RandomAccessFile(new File(manager.getDownloadManager().getDestination(), torrentFile.getPathString()), "rw");
                            file.write(((PieceMessage) message).getBlock(), 0, (int) pos);

                        }else{
                            file.write(((PieceMessage) message).getBlock(), 0, ((PieceMessage) message).getBlock().length);
                        }


                        buf = new byte[BLOCK_SIZE]; //
                        //TorrentFile torrentFile = manager.getTorrent().getInfo().getFileFromPiece(piece.getIndex());
                        int len, read = ((PieceMessage) message).getBlock().length;
                        //while(read < manager.getTorrent().getInfo().getPieceLength()){
                        //    len = in.read(buf);
                        while(read < manager.getTorrent().getInfo().getPieceLength()){
                            len = in.read(buf);
                            read += len;

                            if(pos+len > torrentFile.getLength()){
                                file.write(buf, 0, (int) (torrentFile.getLength()-pos));
                                //WRITE THE LAST BYTES
                                torrentFile = manager.getTorrent().getInfo().getFile(torrentFile.getIndex()+1);
                                pos = len-(torrentFile.getLength()-pos);
                                file = new RandomAccessFile(new File(manager.getDownloadManager().getDestination(), torrentFile.getPathString()), "rw");
                                file.write(buf, 0, (int) pos);
                                continue;
                            }

                            file.write(buf, 0, len);
                            pos += len;

                            //read += len;
                            System.out.println("WRITING: "+len+"  - "+piece.getIndex()+"   "+peer.getHostAddress().getHostAddress());
                            //DETERMINE FILE... - (index*PIECE_LENGTH) - FILE INDEX STARTS 0+ THEN WE MAY NEED TO SPLIT BLOCK IF BETWEEN FILES
                        }


                        System.out.println("COMPLETED:  "+piece.getIndex()+"   "+peer.getHostAddress().getHostAddress());
                        */
                        //ONCE COMPLETE - SET TO NOT REQUESTING
                        //requesting = false;
                        //LISTENER FOR COMPLETE
                    }
                    break;

                case CANCEL:
                    break;

                case PORT:
                    break;

                default:
                    //System.err.println("ERROR  "+mcount+"  "+in.available());//+"  "+message);
                    System.err.println("ERROR    "+id+"  "+in.available()+"       "+peer.getHostAddress().getHostAddress());
                    break;
            }

            System.out.println("MESSAGE: "+type+"       "+peer.getHostAddress().getHostAddress());

        //}else{
        //    socket.setKeepAlive(true); //MAYBE MAYBE NOT FOR THIS...
        }

        if(!bitfield){
            pieces = new boolean[manager.getTorrent().getInfo().getTotalPieces()];
            bitfield = true;
        }
    }

    @Override
    public void send(MessageBase message)throws IOException {
        if(choked){
            return;
        }

        if(message.getType() == MessageType.REQUEST){
            requesting = true;
        }
        out.write(message.encode());
        out.flush();
    }

    public boolean hasBitfield(){
        return bitfield;
    }

    public boolean isChoked(){
        return choked;
    }

    public boolean isRequesting(){
        return requesting;
    }

    @Override
    public void close(){
        //if(piece != null){
        //    manager.getDownloadManager().failedPiece(piece);
        //}
        try{
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
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
