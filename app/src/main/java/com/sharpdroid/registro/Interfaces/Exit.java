package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.Date;

public class Exit implements Serializable {
    private int id;
    private boolean done;
    private Date day;
    private String hours;
    private String justification;

    public Exit(int id, boolean done, Date day, String hours, String justification) {
        this.id = id;
        this.done = done;
        this.day = day;
        this.hours = hours;
        this.justification = justification;
    }

    public int getId() {
        return id;
    }

    public boolean isDone() {
        return done;
    }

    public Date getDay() {
        return day;
    }

    public String getHours() {
        return hours;
    }

    public String getJustification() {
        return justification;
    }
}
