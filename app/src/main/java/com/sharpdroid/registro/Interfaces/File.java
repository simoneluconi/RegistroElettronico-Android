package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class File implements Serializable {
    private String id;
    private String name;
    private String type;
    private String date;
    private String cksum;

    public File(String id, String name, String type, String date, String cksum) {
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

    public String getDate() {
        return date;
    }

    public String getCksum() {
        return cksum;
    }
}
