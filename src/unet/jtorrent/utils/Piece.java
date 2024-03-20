package unet.jtorrent.utils;

import unet.jtorrent.utils.inter.PieceState;

public class Piece {

    public static final int PIECE_LENGTH = 20;

    private int i, offset;
    private byte[] hash;
    private PieceState state = PieceState.WAITING;

    public Piece(byte[] hash, int i){
        if(hash.length != PIECE_LENGTH){
            throw new IllegalArgumentException("Piece hash is not correct length");
        }

        this.hash = new byte[PIECE_LENGTH];
        System.arraycopy(hash, 0, this.hash, 0, this.hash.length);

        this.i = i;
    }

    public byte[] getHash(){
        return hash;
    }

    public int getIndex(){
        return i;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public int getOffset(){
        return offset;
    }

    public void setState(PieceState state){
        this.state = state;
    }

    public PieceState getState(){
        return state;
    }
}
