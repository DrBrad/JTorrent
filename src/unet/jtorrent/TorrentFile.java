package unet.jtorrent;

import unet.bencode.variables.BencodeObject;

import java.util.ArrayList;
import java.util.List;

public class TorrentFile {

    private List<String> path;
    private long length;

    public TorrentFile(BencodeObject ben){
        if(ben.containsKey("length")){
            length = ben.getLong("length");
        }

        if(ben.containsKey("path")){
            path = new ArrayList<>();

            for(int i = 0; i < ben.getBencodeArray("path").size(); i++){
                path.add(ben.getBencodeArray("path").getString(i));
            }
        }
    }

    public long getLength(){
        return length;
    }

    public List<String> getPath(){
        return path;
    }
}
