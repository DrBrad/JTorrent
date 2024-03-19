package unet.jtorrent.utils;

import unet.bencode.variables.BencodeObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class TorrentInfo {

    public static final int PIECE_LENGTH = 20;

    private String name;
    private int pieceLength;
    private long length;
    private byte[] infoHash;
    private List<byte[]> pieces;
    private List<TorrentFile> files;

    public TorrentInfo(BencodeObject ben)throws NoSuchAlgorithmException  {
        if(ben.containsKey("name")){
            name = ben.getString("name");
        }

        if(ben.containsKey("piece length")){
            pieceLength = ben.getInteger("piece length");
        }

        if(ben.containsKey("pieces")){
            pieces = new ArrayList<>();

            for(int i = 0; i < ben.getBytes("pieces").length/PIECE_LENGTH; i++){
                byte[] buf = new byte[20];
                System.arraycopy(ben.getBytes("pieces"), i*20, buf, 0, PIECE_LENGTH);
                pieces.add(buf);
            }
        }

        if(ben.containsKey("files")){
            files = new ArrayList<>();

            for(int i = 0; i < ben.getBencodeArray("files").size(); i++){
                TorrentFile file = new TorrentFile(ben.getBencodeArray("files").getBencodeObject(i));
                files.add(file);
                length += file.getLength();
            }
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        infoHash = digest.digest(ben.encode());
    }

    public String getName(){
        return name;
    }

    public int getPieceLength(){
        return pieceLength;
    }

    public List<byte[]> getPieces(){
        return pieces;
    }

    public int getTotalPieces(){
        return pieces.size();
    }

    public List<TorrentFile> getFiles(){
        return files;
    }

    public byte[] getHash(){
        return infoHash;
    }

    public long getTotalLength(){
        return length;
    }
}
