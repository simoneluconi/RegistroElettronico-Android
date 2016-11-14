package com.sharpdroid.registro.user.Entry;

import java.io.Serializable;

public class Absence implements Serializable {
    private int id;
    private String from;
    private String to;
    private int days;
    private String justification;

    public Absence(int id, String from, String to, int days, String justification) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.days = days;
        this.justification = justification;
    }

    public int getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getDays() {
        return days;
    }

    public String getJustification() {
        return justification;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
