package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.Date;

public class Absence implements Serializable {
    private int id;
    private boolean done;
    private Date from;
    private Date to;
    private int days;
    private String justification;

    public Absence(int id, boolean done, Date from, Date to, int days, String justification) {
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

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public int getDays() {
        return days;
    }

    public String getJustification() {
        return justification;
    }
}
