package unet.jtorrent.utils;

import unet.bencode.io.BencodeReader;
import unet.bencode.variables.BencodeObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Torrent {

    private String comment, createdBy;
    private List<URI> announceList;
    private long creationDate;
    private TorrentInfo info;

    public Torrent(File file){
        try{
            BencodeReader reader = new BencodeReader(new FileInputStream(file));
            BencodeObject ben = (BencodeObject) reader.read();
            reader.close();

            //SHOULD WE JUST IGNORE THIS OR ADD IT TO THE ANNOUNCE LIST...?

            if(ben.containsKey("announce-list")){
                announceList = new ArrayList<>();
                for(int i = 0; i < ben.getBencodeArray("announce-list").size(); i++){
                    try{
                        announceList.add(new URI(ben.getBencodeArray("announce-list").getBencodeArray(i).getString(0)));
                    }catch(URISyntaxException e){
                        e.printStackTrace();
                    }
                }
            }

            if(ben.containsKey("announce")){
                if(announceList == null){
                    announceList = new ArrayList<>();
                }

                try{
                    announceList.add(new URI(ben.getString("announce")));
                }catch(URISyntaxException e){
                    e.printStackTrace();
                }
            }

            if(ben.containsKey("comment")){
                comment = ben.getString("comment");
            }

            if(ben.containsKey("created by")){
                createdBy = ben.getString("created by");
            }

            if(ben.containsKey("creation date")){
                creationDate = ben.getLong("creation date");
            }

            System.out.println(ben);

            if(ben.containsKey("info")){
                info = new TorrentInfo(ben.getBencodeObject("info"));
            }

        }catch(IOException | NoSuchAlgorithmException ex){
            ex.printStackTrace();
        }
    }

    public List<URI> getAnnounceList(){
        return announceList;
    }

    public String getComment(){
        return comment;
    }

    public String getCreatedBy(){
        return createdBy;
    }

    public long getCreationDate(){
        return creationDate;
    }

    public TorrentInfo getInfo(){
        return info;
    }
}
