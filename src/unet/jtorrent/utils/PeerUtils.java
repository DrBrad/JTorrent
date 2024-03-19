package unet.jtorrent.utils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class PeerUtils {

    public static InetSocketAddress unpackAddress(byte[] buf){
        try{
            if(buf.length == 6){
                InetAddress address = InetAddress.getByAddress(new byte[]{
                        buf[0],
                        buf[1],
                        buf[2],
                        buf[3]
                });

                //System.out.println(address.getHostAddress());
                return new InetSocketAddress(address, (buf[4] & 0xff) << 8 | (buf[5] & 0xff));
                //return new InetSocketAddress(address, ((buf[4] << 8) | buf[5] & 0xff));

            }else if(buf.length == 18){
                InetAddress address = InetAddress.getByAddress(new byte[]{
                        buf[0],
                        buf[1],
                        buf[2],
                        buf[3],

                        buf[4],
                        buf[5],
                        buf[6],
                        buf[7],

                        buf[8],
                        buf[9],
                        buf[10],
                        buf[11],

                        buf[12],
                        buf[13],
                        buf[14],
                        buf[15]
                });

                //return new InetSocketAddress(address, ((buf[16] << 8) | buf[17] & 0xff));
                return new InetSocketAddress(address, (buf[16] & 0xff) << 8 | (buf[17] & 0xff));
            }
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
        return null;
    }
}
