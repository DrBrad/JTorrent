package unet.jtorrent.utils.inter;

public enum TrackerTypes {

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

    public static TrackerTypes getFromScheme(String scheme){
        for(TrackerTypes type : TrackerTypes.values()){
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
