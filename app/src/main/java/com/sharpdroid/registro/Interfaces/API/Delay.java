package com.sharpdroid.registro.Interfaces.API;

import java.io.Serializable;
import java.util.Date;

public class Delay implements Serializable {
    private int id;
    private boolean done;
    private Date day;
    private int hour;
    private String justification;

    public Delay(int id, boolean done, Date day, int hour, String justification) {
        this.id = id;
        this.done = done;
        this.day = day;
        this.hour = hour;
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

    public int getHour() {
        return hour;
    }

    public String getJustification() {
        return justification;
    }
}
