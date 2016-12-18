package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Absence implements Serializable {
    private int id;
    private boolean done;
    private String from;
    private String to;
    private int days;
    private String justification;

    public Absence(int id, boolean done, String from, String to, int days, String justification) {
        this.id = id;
        this.done = done;
        this.from = from;
        this.to = to;
        this.days = days;
        this.justification = justification;
    }

    public int getId() {
        return id;
    }

    public boolean isDone() {
        return done;
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
}
