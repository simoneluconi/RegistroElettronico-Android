package com.sharpdroid.registroelettronico.Interfaces.API;

import android.util.Base64;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Exit implements Serializable {
    private int id;
    private boolean done;
    private Date day;
    private int hour;
    private String justification;

    public Exit(int id, boolean done, Date day, int hour, String justification) {
        this.id = id;
        this.done = done;
        this.day = day;
        this.hour = hour;
        this.justification = justification;
    }

    public String getId() {
        try {
            return Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(String.valueOf(id).concat("exit").concat(String.valueOf(done)).concat(day.toString()).concat(String.valueOf(hour)).getBytes()), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
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
