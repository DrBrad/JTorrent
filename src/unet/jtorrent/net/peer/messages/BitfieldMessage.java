package unet.jtorrent.net.peer.messages;

import unet.jtorrent.net.peer.messages.inter.MessageBase;
import unet.jtorrent.net.peer.messages.inter.MessageType;

public class BitfieldMessage extends MessageBase {

    private boolean[] pieces;

    public BitfieldMessage(){
        type = MessageType.BITFIELD;
    }

    @Override
    public byte[] encode(){
        byte[] buf = super.encode();

        for(int i = 0; i < pieces.length; i++){
            buf[5+(i/8)] |= (1 << 7 - (i % 8)); // Set the bit to 1
        }

        return buf;
    }

    @Override
    public void decode(byte[] buf){
        super.decode(buf);

        for(int i = 0; i < buf.length; i++){
            for(int j = 7; j >= 0; j--){
                int pieceIndex = i * 8 + (7 - j);
                if(pieceIndex < pieces.length){
                    pieces[pieceIndex] = ((buf[i] >> j) & 1) == 1;
                }
            }
        }
    }

    @Override
    public int getLength(){
        return super.getLength()+((int) Math.ceil(pieces.length/8.0));
    }

    public void setNumPieces(int numPieces){
        pieces = new boolean[numPieces];
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

    public void setPieces(boolean[] pieces){
        this.pieces = pieces;
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
