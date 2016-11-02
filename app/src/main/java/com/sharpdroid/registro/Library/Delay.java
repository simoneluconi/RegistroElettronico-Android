package com.sharpdroid.registro.Library;

import java.io.Serializable;

public class Delay implements Serializable {
    private int id;
    private String day;
    private String hours;
    private String justification;

    public Delay(int id, String day, String hours, String justification) {
        this.id = id;
        this.day = day;
        this.hours = hours;
        this.justification = justification;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
