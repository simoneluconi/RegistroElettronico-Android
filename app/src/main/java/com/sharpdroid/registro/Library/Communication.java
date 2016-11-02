package com.sharpdroid.registro.Library;

import java.io.Serializable;

class Communication implements Serializable {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
