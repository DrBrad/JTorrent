package unet.jtorrent.net.trackers.udp.client;

import unet.jtorrent.net.trackers.udp.Call;
import unet.jtorrent.net.trackers.udp.ResponseCallback;
import unet.jtorrent.net.trackers.udp.messages.*;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageAction;
import unet.jtorrent.net.trackers.udp.messages.inter.MessageBase;
import unet.kad4.utils.ByteWrapper;
import unet.kad4.utils.net.AddressUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UTrackerSocket {

    //BEP 15
    public static final int TID_LENGTH = 4;

    //ACTION_ID
    //TRANSACTION_ID

    //private InetSocketAddress address;
    private SecureRandom random;
    private ResponseTracker tracker;
    private DatagramSocket socket;

    public UTrackerSocket(){
        //URI uri = new URI(link);
        //address = new InetSocketAddress(InetAddress.getByName(uri.getHost()), uri.getPort());
        tracker = new ResponseTracker(this);

        try{
            random = SecureRandom.getInstance("SHA1PRNG");
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    public void start()throws SocketException {
        start(0);
    }

    public void start(int port)throws SocketException {
        if(isRunning()){
            throw new IllegalArgumentException("Server has already started.");
        }

        socket = new DatagramSocket(port);

        System.out.println("Started UDP Tracker Client");

        new Thread(new Runnable(){
            @Override
            public void run(){
                while(!socket.isClosed()){
                    try{
                        DatagramPacket packet = new DatagramPacket(new byte[65508], 65508);
                        socket.receive(packet);

                        if(packet != null){
                            new Thread(new Runnable(){
                                @Override
                                public void run(){
                                    onReceive(packet);
                                    tracker.removeStalled();
                                }
                            }).start();
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stop(){
        if(!isRunning()){
            throw new IllegalArgumentException("Server is not currently running.");
        }
        socket.close();
    }

    public boolean isRunning(){
        return (socket != null && !socket.isClosed());
    }

    public int getPort(){
        return (socket != null) ? socket.getLocalPort() : 0;
    }

    private void onReceive(DatagramPacket packet){
        if(AddressUtils.isBogon(packet.getAddress(), packet.getPort())){
            return;
        }

        byte[] buf = packet.getData();

        /*
        Offset  Size            Name            Value
        0       32-bit integer  action          0 // connect
        4       32-bit integer  transaction_id
        */
        MessageAction action = MessageAction.fromCode(((buf[0] & 0xff) << 24) |
                ((buf[1] & 0xff) << 16) |
                ((buf[2] & 0xff) << 8) |
                (buf[3] & 0xff));

        byte[] tid = new byte[4];
        System.arraycopy(buf, 4, tid, 0, tid.length);

        Call call = tracker.poll(new ByteWrapper(tid));

        if(call == null){
            return;
        }

        MessageBase response;

        switch(action){
            case CONNECT:
                response = new ConnectResponse(tid);
                break;

            case ANNOUNCE:
                response = new AnnounceResponse(tid);
                break;

            case SCRAPE:
                response = new ScrapeResponse(tid, ((ScrapeRequest) call.getMessage()).getInfoHashesSize());
                break;

            case ERROR:
                response = new ErrorResponse(tid);
                response.setOrigin(packet.getAddress(), packet.getPort());
                response.decode(buf, packet.getOffset()+8, packet.getLength()-9);
                call.getCallback().onErrorResponse((ErrorResponse) response);
                return;

            default:
                return;
        }

        response.setOrigin(packet.getAddress(), packet.getPort());
        response.decode(buf, packet.getOffset()+8, packet.getLength()-9);
        call.getCallback().onResponse(response);
    }

    public void send(MessageBase message, ResponseCallback callback)throws IOException {
        if(message.getDestination() == null){
            throw new IllegalArgumentException("Message destination set to null");
        }

        if(AddressUtils.isBogon(message.getDestination())){
            return;
        }

        byte[] tid = generateTransactionID();
        message.setTransactionID(tid);
        tracker.add(new ByteWrapper(tid), new Call(message, callback));

        byte[] data = message.encode();
        socket.send(new DatagramPacket(data, 0, data.length, message.getDestination()));
    }

    protected void retry(Call call)throws IOException {
        byte[] data = call.getMessage().encode();
        call.setAttempted();
        socket.send(new DatagramPacket(data, 0, data.length, call.getMessage().getDestination()));
    }

    private byte[] generateTransactionID(){
        byte[] tid = new byte[TID_LENGTH];
        random.nextBytes(tid);
        return tid;
    }
}
