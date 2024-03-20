package unet.jtorrent.net.tunnel.messages.inter;

public enum MessageType {
    /*
    https://wiki.theory.org/BitTorrentSpecification#Messages

    0 - choke
    1 - unchoke
    2 - interested
    3 - not interested
    4 - have
    5 - bitfield
    6 - request
    7 - piece
    8 - cancel
    */

    KEEP_ALIVE {
        public int getLength(){
            return 0;
        }
    },
    CHOKE {
        public int getLength(){
            return 1;
        }

        public byte getID(){
            return 0;
        }
    },
    UNCHOKE {
        public int getLength(){
            return 1;
        }

        public byte getID(){
            return 1;
        }
    },
    INTERESTED {
        public int getLength(){
            return 2;
        }

        public byte getID(){
            return 2;
        }
    },
    NOT_INTERESTED {
        public int getLength(){
            return 3;
        }

        public byte getID(){
            return 3;
        }
    },
    HAVE {
        public int getLength(){
            return 5;
        }

        public byte getID(){
            return 4;
        }
    },
    BITFIELD {
        public int getLength(){ //+X
            return 1;
        }

        public byte getID(){
            return 5;
        }
    },
    REQUEST {
        public int getLength(){ //<index><begin><length>
            return 13;
        }

        public byte getID(){
            return 6;
        }
    },
    PIECE {
        public int getLength(){ //+X  <index><begin><block>
            return 9;
        }

        public byte getID(){
            return 7;
        }
    },
    CANCEL {
        public int getLength(){ //<index><begin><length>
            return 13;
        }

        public byte getID(){
            return 8;
        }
    },
    PORT {
        public int getLength(){ //<listen-port>
            return 3;
        }

        public byte getID(){
            return 9;
        }
    }, INVALID;

    public static MessageType getFromID(byte code){
        for(MessageType message : values()){
            if(code == message.getID()){
                return message;
            }
        }

        return INVALID;
    }

    public int getLength(){
        return -1;
    }

    public byte getID(){
        return -1;
    }
}
