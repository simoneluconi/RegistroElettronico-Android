package com.sharpdroid.registroelettronico.Interfaces.API;

import android.util.Base64;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public String getId() {
        try {
            return Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest("absence".concat(String.valueOf(id).concat(String.valueOf(done)).concat(from.toString()).concat(to.toString()).concat(String.valueOf(days))).getBytes()), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
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
