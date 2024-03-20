package unet.jtorrent.utils;

import unet.jtorrent.utils.inter.PieceState;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class DownloadManager {

    private Torrent torrent;
    private File destination;
    //private boolean[] completed;
    //private List<Piece> downloading, waiting;
    private int numCompleted;
    private long downloaded = 0, uploaded = 0;

    public DownloadManager(Torrent torrent, File destination){
        //waiting = new ArrayList<>();
        this.torrent = torrent;
        this.destination = new File(destination, torrent.getInfo().getName());
        //downloading = new ArrayList<>();
        //completed = new boolean[torrent.getInfo().getTotalPieces()];
    }

    public void createFiles(){
        for(TorrentFile f : torrent.getInfo().getFiles()){
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

    public long getDownloaded(){
        return downloaded;
    }

    public long getLeft(){
        return torrent.getInfo().getTotalLength()-downloaded;
    }

    public long getUploaded(){
        return uploaded;
    }
}
