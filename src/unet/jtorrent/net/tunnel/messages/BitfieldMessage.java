package unet.jtorrent.net.tunnel.messages;

import unet.jtorrent.net.tunnel.messages.inter.MessageBase;
import unet.jtorrent.net.tunnel.messages.inter.MessageType;

public class BitfieldMessage extends MessageBase {

    private boolean[] pieces;

    public BitfieldMessage(){
        type = MessageType.BITFIELD;
    }

    public BitfieldMessage(int numPieces){
        type = MessageType.BITFIELD;
        pieces = new boolean[numPieces];
    }

    @Override
    public byte[] encode(){
        return new byte[0];
    }

    @Override
    public void decode(byte[] buf){
        for(int i = 0; i < buf.length; i++){
            for(int j = 7; j >= 0; j--){
                int pieceIndex = i * 8 + (7 - j);
                if(pieceIndex < pieces.length){
                    boolean hasPiece = ((buf[i] >> j) & 1) == 1;
                    pieces[pieceIndex] = hasPiece;
                }
            }
        }
    }

    public void setPiece(int i, boolean piece){
        pieces[i] = piece;
    }

    public boolean getPiece(int i){
        return pieces[i];
    }

    public int getTotalPieces(){
        return pieces.length;
    }

    public boolean[] getPieces(){
        return pieces;
    }

    @Override
    public String toString(){
        char[] c = new char[pieces.length];
        for(int i = 0; i < pieces.length; i++){
            c[i] = (pieces[i]) ? '■' : '□';
        }

        return String.valueOf(c);
    }
}
