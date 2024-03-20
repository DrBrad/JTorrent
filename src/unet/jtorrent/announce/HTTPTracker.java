package unet.jtorrent.announce;

import unet.bencode.io.BencodeReader;
import unet.bencode.variables.BencodeObject;
import unet.jtorrent.TorrentClient;
import unet.jtorrent.announce.inter.PeerListener;
import unet.jtorrent.announce.inter.Tracker;
import unet.jtorrent.announce.inter.AnnounceEvent;
import unet.jtorrent.utils.Peer;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.utils.PeerUtils;
import unet.jtorrent.utils.TorrentManager;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class HTTPTracker extends Tracker {

    private URI uri;

    public HTTPTracker(TorrentManager manager, URI uri){
        super(manager);
        this.uri = uri;
    }

    @Override
    public void announce(AnnounceEvent event){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    String url = String.format("%s?info_hash=%s&peer_id=%s&downloaded=%s&left=%s&uploaded=%s&event=%s&num_want=%s&port=%s",
                            uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort()+"/announce",
                            encodeHexString(manager.getTorrent().getInfo().getHash()),
                            encodeHexString(manager.getClient().getPeerID()), //GRAB FROM CLIENT...
                            manager.getDownloadManager().getDownloaded(),
                            manager.getDownloadManager().getLeft(),
                            manager.getDownloadManager().getUploaded(),
                            event.getName(),
                            manager.getClient().getMaxPeersPerRequest(),
                            manager.getClient().getTCPPort());

                    System.out.println(url);

                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");

                    BencodeReader reader = new BencodeReader(connection.getInputStream());
                    BencodeObject ben = (BencodeObject) reader.read();
                    reader.close();

                    connection.disconnect();


                    byte[] addr;
                    /*
                    if(origin.getAddress() instanceof Inet4Address){
                        addr = new byte[6];

                    }else{
                        addr = new byte[18];
                    }
                    */

                    List<Peer> peersList = new ArrayList<>();
                    addr = new byte[6];

                    int position = 0;
                    while(position < ben.getBytes("peers").length){
                        System.arraycopy(ben.getBytes("peers"), position, addr, 0, addr.length);
                        peersList.add(PeerUtils.unpackAddress(addr));
                        position += addr.length;
                    }

                    peers += peersList.size();
                    System.out.println("HTTP: "+uri.toString()+" GOT PEERS: "+peers);

                    if(!listeners.isEmpty()){
                        for(PeerListener listener : listeners){
                            listener.onPeersReceived(peersList);
                        }
                    }

                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void scrape(){

    }

    private String encodeHexString(byte[] buf){
        StringBuilder result = new StringBuilder();
        for(byte b : buf){
            result.append('%').append(String.format("%02X", b));
        }
        return result.toString();
    }

    private byte[] stringToHex(String s){
        byte[] b = new byte[s.length()/2];
        for(int i = 0; i < b.length; i += 2){
            b[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+Character.digit(s.charAt(i+1), 16));
        }

        return b;
    }
}
