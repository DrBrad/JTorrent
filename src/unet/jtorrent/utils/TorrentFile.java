package unet.jtorrent.utils;

import unet.bencode.variables.BencodeObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TorrentFile {

    private int i;
    private List<String> path;
    private long length;

    public TorrentFile(BencodeObject ben, int i){
        this.i = i;

        if(ben.containsKey("length")){
            length = ben.getLong("length");
        }

        if(ben.containsKey("path")){
            path = new ArrayList<>();

            for(int j = 0; j < ben.getBencodeArray("path").size(); j++){
                path.add(ben.getBencodeArray("path").getString(j));
            }
        }
    }

    public int getIndex(){
        return i;
    }

    public long getLength(){
        return length;
    }

    public String getPathString(){
        StringBuilder builder = new StringBuilder();
        for(String p : path){
            builder.append("/");
            builder.append(p);
        }

        return builder.toString();
    }

    public List<String> getPath(){
        return path;
    }
}
