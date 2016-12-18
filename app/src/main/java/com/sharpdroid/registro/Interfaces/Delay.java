package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Delay implements Serializable {
    private int id;
    private boolean done;
    private String day;
    private String hours;
    private String justification;

    public Delay(int id, boolean done, String day, String hours, String justification) {
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

    public String getDay() {
        return day;
    }

    public String getHours() {
        return hours;
    }

    public String getJustification() {
        return justification;
    }
}
