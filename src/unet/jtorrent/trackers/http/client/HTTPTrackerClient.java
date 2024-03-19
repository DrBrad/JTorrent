package unet.jtorrent.trackers.http.client;

import unet.bencode.io.BencodeReader;
import unet.bencode.variables.BencodeObject;
import unet.jtorrent.TorrentClient;
import unet.jtorrent.trackers.inter.TrackerClient;
import unet.jtorrent.trackers.inter.AnnounceEvent;
import unet.jtorrent.utils.Torrent;
import unet.jtorrent.utils.PeerUtils;

import java.io.IOException;
import java.net.*;

public class HTTPTrackerClient extends TrackerClient {

    private URI uri;

    public HTTPTrackerClient(TorrentClient client, Torrent torrent, URI uri){
        super(client, torrent);
        this.uri = uri;
    }

    @Override
    public void announce(AnnounceEvent event){
        try{
            String url = String.format("%s?info_hash=%s&peer_id=%s&downloaded=%s&left=%s&uploaded=%s&event=%s&num_want=%s&port=%s",
                    uri.getScheme()+"://"+uri.getHost()+":"+uri.getPort()+"/announce",
                    encodeHexString(torrent.getInfo().getHash()),
                    encodeHexString(client.getPeerID()), //GRAB FROM CLIENT...
                    torrent.getDownloaded(),
                    torrent.getLeft(),
                    torrent.getUploaded(),
                    event.getName(),
                    numWant,
                    client.getTCPPort());

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
            addr = new byte[6];

            int position = 0;
            while(position < ben.getBytes("peers").length){
                System.arraycopy(ben.getBytes("peers"), position, addr, 0, addr.length);
                peers.add(PeerUtils.unpackAddress(addr));
                position += addr.length;
            }

            for(InetSocketAddress address : peers){
                //System.out.println("HTTP: "+address.getAddress().getHostAddress()+" : "+address.getPort());
            }

            System.out.println("HTTP: "+uri.toString()+" GOT PEERS: "+peers.size());

        }catch(IOException e){
            e.printStackTrace();
        }
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
