package unet.jtorrent;

import unet.jtorrent.kad.messages.*;
import unet.kad4.Kademlia;

import java.io.File;

public class Main {

    public static void main(String[] args)throws Exception {
        /*
        Kademlia kad = new Kademlia();

        //BEP 5
        kad.registerMessage(AnnouncePeerRequest.class);
        kad.registerMessage(AnnouncePeerResponse.class);

        //BEP 5
        kad.registerMessage(GetRequest.class);
        kad.registerMessage(GetResponse.class);

        //BEP 5
        kad.registerMessage(PutRequest.class);
        kad.registerMessage(PutResponse.class);

        //BEP 5
        kad.registerMessage(GetPeersRequest.class);
        kad.registerMessage(GetPeersResponse.class);

        //BEP 51
        kad.registerMessage(SampleHashRequest.class);
        kad.registerMessage(SampleHashResponse.class);
        */

        Torrent torrent = new Torrent(new File("/home/brad/Downloads/torrent.torrent"));
        System.out.println(torrent.getAnnounce());
        System.out.println(torrent.getComment());
        System.out.println(torrent.getCreatedBy());
        System.out.println(torrent.getInfo().getFiles().get(0).getPath().get(0));

        StringBuilder builder = new StringBuilder();
        for(byte b : torrent.getInfo().getHash()){
            builder.append(String.format("%02x", b));
        }


        /*
        for(byte[] piece : torrent.getInfo().getPieces()){
            System.out.println("PIECE: "+bytesToHex(piece));
        }
        */


        /*

        //new DHTTracker(torrent.getInfoHash());

        UDPClient client = new UDPClient();
        try{
            client.start(6969);

            for(String announce : torrent.getAnnounceList()){
                try{
                    URI uri = new URI(announce);
                    if(!uri.getScheme().equals("udp")){
                        continue;
                    }

                    System.out.println("UDP SENDING:  "+InetAddress.getByName(uri.getHost()).getHostAddress()+"  "+uri.getPort());

                    ConnectRequest request = new ConnectRequest();
                    request.setDestination(InetAddress.getByName(uri.getHost()), uri.getPort());
                    client.send(request, new ResponseCallback(){
                        @Override
                        public void onResponse(MessageBase message){
                            ConnectResponse response = (ConnectResponse) message;
                            System.out.println("RESPONSE: "+response.getConnectionID());

                            //DO THE REQUESTS BETTER...?
                            AnnounceRequest request1 = new AnnounceRequest();
                            request1.setDestination(request.getDestination());
                            request1.setConnectionID(response.getConnectionID());
                            request1.setEvent(AnnounceEvent.STARTED);
                            request1.setInfoHash(torrent.getInfo().getHash()); //9e8cb640823822be312c1278089c96cafacc8627
                            request1.setPeerID(stringToHex("2d5452333030302d326f71727270786231303232"));
                            request1.setDownloaded(0);
                            request1.setLeft(1880132108); //MUST CALC THE AMMOUNT WE NEED...
                            request1.setUploaded(0);
                            request1.setNumWant(80);
                            request1.setKey(330182370);
                            request1.setPort(8080); //TCP PORT

                            try{
                                client.send(request1, new ResponseCallback(){
                                    @Override
                                    public void onResponse(MessageBase message){
                                        AnnounceResponse response = (AnnounceResponse) message;

                                        System.out.println(response.getInterval()+"  "+response.getLeachers()+"  "+response.getSeeders());
                                    }
                                });
                            }catch(IOException ex){
                                ex.printStackTrace();
                            }

                        }
                    });
                }catch(URISyntaxException ex){
                    ex.printStackTrace();
                }
            }

        }catch(IOException ex){
            ex.printStackTrace();
        }
        */
    }
}
