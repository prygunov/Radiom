package net.artux.radio.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Stream extends Item implements Serializable {

    public boolean ads;
    public String ganre;
    public Map<String, String> types = new LinkedHashMap<>();
    public MediaData mediaData;

    public static class MediaData implements Serializable{

        public String artist;
        public String title;

        public boolean isEmpty(){
            return artist==null || title == null;
        }

    }
}
