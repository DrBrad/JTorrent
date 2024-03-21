package unet.jtorrent.utils;

import unet.jtorrent.net.peer.inter.PeerSocket;
import unet.jtorrent.net.peer.messages.RequestMessage;
import unet.jtorrent.utils.inter.PieceState;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class DownloadManager {

    public static final int BLOCK_SIZE = 4096;
    private TorrentManager manager;
    private File destination;
    //private boolean[] completed;
    //private List<Piece> downloading, waiting;
    private int numCompleted;
    private long downloaded = 0, uploaded = 0;

    public DownloadManager(TorrentManager manager, File destination){
        //waiting = new ArrayList<>();
        this.manager = manager;
        this.destination = new File(destination, manager.getTorrent().getInfo().getName());
        //downloading = new ArrayList<>();
        //completed = new boolean[torrent.getInfo().getTotalPieces()];
    }

    public void createFiles(){
        for(TorrentFile f : manager.getTorrent().getInfo().getFiles()){
            StringBuilder path = new StringBuilder();
            for(String p : f.getPath()){
                path.append("/"+p);
            }

            File file = new File(destination, path.toString());
            file.getParentFile().mkdirs();

            if(!file.exists()){
                try{
                    file.createNewFile();

                    RandomAccessFile r = new RandomAccessFile(file, "rw");
                    r.setLength(f.getLength());

                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isInterested(boolean[] available){
        for(int i = 0; i < available.length; i++){
            if(!available[i]){
                continue;
            }

            switch(manager.getTorrent().getInfo().getPiece(i).getState()){
                case WAITING:
                case STOPPED:
                    return true;
            }
        }
        return false;
    }

    public void downloadPieces(){
        /*
        for(Piece piece : manager.getTorrent().getInfo().getPieces()){
            downloadPiece(piece);
        }
        */
    }

    public void downloadPiece(PeerSocket socket){
        Piece piece = null;
        for(int i = 0; i < socket.getPieces().length; i++){
            if(socket.getPieces()[i]){
                Piece p = manager.getTorrent().getInfo().getPiece(i);
                if(p.getState() == PieceState.WAITING ||
                        p.getState() == PieceState.STOPPED){
                    piece = p;
                    break;
                }
            }
        }

        if(piece == null){
            return;
        }

        piece.setState(PieceState.DOWNLOADING);

        RequestMessage message = new RequestMessage();
        message.setIndex(piece.getIndex());
        message.setBegin(piece.getOffset()); //WE COULD BEGIN BASED OFF OF WHERE WE LEFT OFF BUT THIS SEEMS LIKE IT WOULD BE INVALID ANYWAYS...
        message.setLength(BLOCK_SIZE);

        try{
            socket.send(message);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("SENT REQUEST FOR: "+piece.getIndex()+"       "+socket.getPeer().getHostAddress().getHostAddress());
    }

    private void downloadPiece(Piece piece){
        for(PeerSocket socket : manager.getConnectionManager().getConnections()){
            if(socket.getPieces()[piece.getIndex()]){
                RequestMessage message = new RequestMessage();
                message.setIndex(piece.getIndex());
                message.setBegin(piece.getOffset()); //WE COULD BEGIN BASED OFF OF WHERE WE LEFT OFF BUT THIS SEEMS LIKE IT WOULD BE INVALID ANYWAYS...
                message.setLength(BLOCK_SIZE);

                try{
                    socket.send(message);
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    /*
    public Piece startPiece(boolean[] available){
        if(isComplete()){
            throw new IllegalArgumentException("Torrent is complete.");
        }

        for(int i = 0; i < available.length; i++){
            if(available[i]){
                Piece piece = torrent.getInfo().getPiece(i);
                switch(piece.getState()){
                    case STOPPED:
                    case WAITING:
                        piece.setState(PieceState.DOWNLOADING);
                        return piece;
                }
            }
        }

        return null;
    }

    public boolean isComplete(){
        return (numCompleted == torrent.getInfo().getTotalPieces());
    }

    public void completedPiece(int i){
        torrent.getInfo().getPiece(i).setState(PieceState.COMPLETE);
        numCompleted++;
    }

    public void stopPiece(int i){
        torrent.getInfo().getPiece(i).setState(PieceState.STOPPED);
    }

    public PieceState getState(int i){
        return torrent.getInfo().getPiece(i).getState();
    }

    public int getTotalCompleted(){
        return numCompleted;
    }

    public File getDestination(){
        return destination;
    }

    /*
    public File getFileOffset(int off){
        int current = 0;
        TorrentFile p = torrent.getInfo().getFile(0);
        for(TorrentFile f : torrent.getInfo().getFiles()){
            if(off < current+f.getLength()){
                break;
            }
            p = f;
        }

        StringBuilder path = new StringBuilder();
        for(String g : p.getPath()){
            path.append("/"+g);
        }

        return new File(destination, path.toString());
    }

    /*
    public synchronized void setCompleted(int i){
        //completed[i] = true;
        numCompleted++;
    }

    public synchronized boolean isCompleted(int i){
        //return completed[i];
    }
    */

    public synchronized int getTotalCompleted(){
        return numCompleted;
    }

    /*
    public synchronized Piece pollPiece(){
        if(waiting.isEmpty()){
            return null;
        }

        Piece piece = waiting.get(0);
        waiting.remove(0);
        downloading.add(piece);

        return piece; //RETURN INDEX
    }

    public synchronized void completedPiece(Piece piece){
        downloading.remove(piece);
    }

    public synchronized void failedPiece(Piece piece){
        //IF PIECE FAILED DO TO (EXAMPLE: BROKEN SOCKET) REDO THE PIECE
        downloading.remove(piece);
        waiting.add(piece);
    }
    */

    public void verify(){
        //VERIFY ALL OF THE PIECES...
    }

    public File getDestination(){
        return destination;
    }

    public long getDownloaded(){
        return downloaded;
    }

    public long getLeft(){
        return manager.getTorrent().getInfo().getTotalLength()-downloaded;
    }

    public long getUploaded(){
        return uploaded;
    }
}
