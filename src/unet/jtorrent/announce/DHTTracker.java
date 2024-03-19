package unet.jtorrent.announce;

import unet.kad4.utils.Node;

import java.util.List;

public class DHTTracker {

    private byte[] infoHash;

    //WHY IS THIS CAUSING MULTIPLE RESTARTS...
    //WHY IS THE SERVER UNREACHABLE...
    private List<Node> connected;

    public DHTTracker(byte[] infoHash){
        this.infoHash = infoHash;

        //connected = kad.getRoutingTable().findClosest(new UID(infoHash), KBucket.MAX_BUCKET_SIZE);
        getPeers(connected, 0);


        //THEN ANNOUNCE...
    }

    public void getPeers(List<Node> nodes, int attempts){
        /*
        for(Node node : nodes){
            GetPeersRequest request = new GetPeersRequest();
            request.setDestination(node.getAddress());
            request.setInfoHash(infoHash);

            try{
                kad.getServer().send(request, node, new ResponseCallback(){
                    @Override
                    public void onResponse(ResponseEvent event){
                        kad.getRoutingTable().insert(node);
                        System.out.println("GET_PEERS "+node);
                        GetPeersResponse response = (GetPeersResponse) event.getMessage();
                        if(response.getToken() == null){
                            return;
                        }

                        if(response.hasNodes()){
                            if(attempts > 2){
                                return;
                            }

                            List<Node> nodes = response.getAllNodes();

                            for(int i = nodes.size()-1; i > -1; i--){
                                if(connected.contains(node)){
                                    nodes.remove(node);
                                }
                            }

                            connected.addAll(nodes);

                            getPeers(nodes, attempts+1);
                        }
                    }
                });

            }catch(IOException e){
                e.printStackTrace();
            }
        }
        */
    }
}
