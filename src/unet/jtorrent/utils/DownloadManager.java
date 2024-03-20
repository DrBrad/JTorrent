package unet.jtorrent.utils;

import unet.jtorrent.utils.inter.PieceState;

import java.util.ArrayList;
import java.util.List;

public class DownloadManager {

    private Torrent torrent;
    //private boolean[] completed;
    //private List<Piece> downloading, waiting;
    private int numCompleted;
    private long downloaded = 0, uploaded = 0;

    public DownloadManager(Torrent torrent){
        //waiting = new ArrayList<>();
        this.torrent = torrent;
        //downloading = new ArrayList<>();
        //completed = new boolean[torrent.getInfo().getTotalPieces()];
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
