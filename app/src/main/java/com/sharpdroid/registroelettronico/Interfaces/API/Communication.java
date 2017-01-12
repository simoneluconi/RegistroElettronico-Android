package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;
import java.util.Date;

public class Communication implements Serializable {
    private String title;
    private String type;
    private Date date;
    private int id;

    public Communication(String title, String type, Date date, int id) {
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

    public Date getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
}
