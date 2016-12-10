package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Communication implements Serializable {
    private String title;
    private String type;
    private String date;
    private int id;

    public Communication(String title, String type, String date, int id) {
        this.title = title;
        this.type = type;
        this.date = date;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
}
