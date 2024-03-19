package unet.jtorrent.trackers.inter;

public enum AnnounceEvent {

    NONE {
        public String getName(){
            return "none";
        }

        public int getCode(){
            return 0;
        }
    },
    COMPLETED {
        public String getName(){
            return "completed";
        }

        public int getCode(){
            return 1;
        }
    },
    STARTED {
        public String getName(){
            return "started";
        }

        public int getCode(){
            return 2;
        }
    },
    STOPPED {
        public String getName(){
            return "stopped";
        }

        public int getCode(){
            return 3;
        }
    };

    public String getName(){
        return null;
    }

    public int getCode(){
        return 0;
    }
}
