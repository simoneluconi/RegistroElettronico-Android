package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;
import java.util.Date;

public class File implements Serializable {
    private String id;
    private String name;
    private String type;
    private Date date;
    private String cksum;
    private String link;
    private boolean hidden;

    public File(String id, String name, String type, Date date, String cksum, String link, boolean hidden) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.date = date;
        this.cksum = cksum;
        this.link = link;
        this.hidden = hidden;
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

    public String getLink() {
        return link;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isLink() {
        return type.equals("link");
    }
}
