package unet.jtorrent.utils;

import java.util.ArrayList;
import java.util.List;

public class DownloadManager {

    private List<Piece> downloading, waiting;

    public DownloadManager(List<Piece> pieces){
        downloading = new ArrayList<>();
        waiting = new ArrayList<>();
        waiting.addAll(pieces);
    }

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

    public synchronized void verify(){
        //VERIFY ALL OF THE PIECES...
    }
}
