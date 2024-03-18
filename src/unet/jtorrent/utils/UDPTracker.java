package unet.jtorrent.utils;

import unet.jtorrent.trackers.udp.ResponseCallback;
import unet.jtorrent.trackers.udp.client.UDPClient;
import unet.jtorrent.trackers.udp.messages.ConnectRequest;
import unet.jtorrent.trackers.udp.messages.ConnectResponse;
import unet.jtorrent.trackers.udp.messages.inter.MessageBase;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

public class UDPTracker implements TrackerURI {

    private boolean alive;
    private long connectionID;
    private int key;

    public UDPTracker(URI uri){

    }

    private void connect(UDPClient client){
        ConnectRequest request = new ConnectRequest();
        //request.setDestination(InetAddress.getByName(uri.getHost()), uri.getPort());
        try{
            client.send(request, new ResponseCallback(){
                @Override
                public void onResponse(MessageBase message){
                    ConnectResponse response = (ConnectResponse) message;
                    connectionID = response.getConnectionID();
                    announce(client);
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void announce(UDPClient client){

    }

    public void scrape(UDPClient client){

    }

    @Override
    public void announce() {

    }

    @Override
    public void scrape() {

    }

    /*
    private String host;
    private TrackerType type;
    private InetAddress address;
    private int port;

    public Tracker(String link)throws URISyntaxException, UnknownHostException {
        URI uri = new URI(link);
        host = uri.getHost();
        type = TrackerType.getFromScheme(uri.getScheme());
        address = InetAddress.getByName(uri.getHost());
        port = uri.getPort();
    }

    public void announce(){

    }

    public void scrape(){

    }
    */





    /*

    public String getHostName(){
        return host;
    }

    public TrackerType getType(){
        return type;
    }

    public InetAddress getAddress(){
        return address;
    }

    public int getPort(){
        return port;
    }

    public enum TrackerType {
        UDP {
            public String getScheme(){
                return "udp";
            }
        },
        HTTP {
            public String getScheme(){
                return "http";
            }
        }, INVALID;

        public static TrackerType getFromScheme(String scheme){
            for(TrackerType type : TrackerType.values()){
                if(scheme.equals(type.getScheme())){
                    return type;
                }
            }

            return INVALID;
        }

        public String getScheme(){
            return null;
        }
    }
    */
}
