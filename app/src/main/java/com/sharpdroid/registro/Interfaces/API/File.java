package com.sharpdroid.registro.Interfaces.API;

import java.io.Serializable;
import java.util.Date;

public class File implements Serializable {
    private String id;
    private String name;
    private String type;
    private Date date;
    private String cksum;

    public File(String id, String name, String type, Date date, String cksum) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.cksum = cksum;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getCksum() {
        return cksum;
    }

    public boolean isLink() {
        return type.equals("link");
    }
}
