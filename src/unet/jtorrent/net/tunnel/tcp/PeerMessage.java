package unet.jtorrent.net.tunnel.tcp;

public enum PeerMessage {
    /*
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
    CHOKE {
        public int getCode(){
            return 0;
        }
    },
    UNCHOKE {
        public int getCode(){
            return 1;
        }
    },
    INTERESTED {
        public int getCode(){
            return 2;
        }
    },
    NOT_INTERESTED {
        public int getCode(){
            return 3;
        }
    },
    HAVE {
        public int getCode(){
            return 4;
        }
    },
    BITFIELD {
        public int getCode(){
            return 5;
        }
    },
    REQUEST {
        public int getCode(){
            return 6;
        }
    },
    PIECE {
        public int getCode(){
            return 7;
        }
    },
    CANCEL {
        public int getCode(){
            return 8;
        }
    }, INVALID;

    public PeerMessage getFromCode(int code){
        for(PeerMessage message : values()){
            if(code == message.getCode()){
                return message;
            }
        }

        return INVALID;
    }

    public int getCode(){
        return -1;
    }
}
