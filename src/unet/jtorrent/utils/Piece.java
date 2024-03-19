package unet.jtorrent.utils;

public class Piece {

    public static final int PIECE_LENGTH = 20;

    private int i, offset;
    private byte[] hash;

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
}
