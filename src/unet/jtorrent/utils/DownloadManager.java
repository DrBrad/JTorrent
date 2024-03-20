package unet.jtorrent.utils;

import java.util.ArrayList;
import java.util.List;

public class DownloadManager {

    private Torrent torrent;
    private boolean[] completed;
    //private List<Piece> downloading, waiting;
    private int numCompleted;
    private long downloaded = 0, uploaded = 0;

    public DownloadManager(Torrent torrent){
        //waiting = new ArrayList<>();
        this.torrent = torrent;
        //downloading = new ArrayList<>();
        completed = new boolean[torrent.getInfo().getTotalPieces()];
    }

    public synchronized void setCompleted(int i){
        completed[i] = true;
        numCompleted++;
    }

    public synchronized boolean isCompleted(int i){
        return completed[i];
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

    public synchronized void verify(){
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
