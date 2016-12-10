package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Delay implements Serializable {
    private int id;
    private String day;
    private String hours;
    private String justification;
    private boolean done;

    public Delay(int id, String day, String hours, String justification, boolean done) {
        this.id = id;
        this.day = day;
        this.hours = hours;
        this.justification = justification;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public String getDay() {
        return day;
    }

    public String getHours() {
        return hours;
    }

    public String getJustification() {
        return justification;
    }

    public boolean isDone() {
        return done;
    }

}
