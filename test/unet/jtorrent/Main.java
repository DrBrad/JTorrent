package unet.jtorrent;

import java.io.File;

public class Main {

    //WORK ON STALE MANAGER FOR CLIENT CONNECTION...

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

        TorrentClient manager = new TorrentClient();
        manager.setMaxPeersPerRequest(80);
        manager.start();

        manager.startTorrent(new File("/home/brad/Downloads/torrent.torrent"), new File("/home/brad/Downloads/test"));
    }
}
