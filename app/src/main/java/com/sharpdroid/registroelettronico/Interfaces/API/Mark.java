package com.sharpdroid.registroelettronico.Interfaces.API;

import android.util.Base64;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Mark implements Serializable {
    public static final String PRIMO_PERIODO = "q1";
    public static final String SECONDO_PERIODO = "q3";
    private String q;
    private boolean ns;
    private String type;
    private Date date;
    private String mark;
    private String desc;


    public Mark(String q, boolean ns, String type, Date date, String mark, String desc) {
        this.q = q;
        this.ns = ns;
        this.type = type;
        this.date = date;
        this.mark = mark;
        this.desc = desc;
    }

    public String getQ() {
        return q;
    }

    public boolean isNs() {
        return ns;
    }

    public void setNs(boolean isNs) {
        ns = isNs;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getMark() {
        return mark;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isNumeric() {
        return mark.matches("[0-9]+(.[0-9]+)?"); //returns true if either 7 or 7.75
    }

    public String getHash() {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(q.concat(type).concat(date.toString()).concat(mark).concat(desc).getBytes());
            return Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
