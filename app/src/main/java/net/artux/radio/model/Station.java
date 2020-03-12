package net.artux.radio.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Station extends Item implements Serializable {

    public int id;
    public int type;
    public String desc;
    public String loc;
    public String url;
    public List<Stream> streams = new ArrayList<>();

}
