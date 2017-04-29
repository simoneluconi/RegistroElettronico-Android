package com.sharpdroid.registroelettronico.Interfaces.API;

import android.util.Base64;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Lesson implements Serializable {
    private String teacher;
    private Date date;
    private String content;

    public Lesson(String teacher, Date date, String content) {
        this.teacher = teacher;
        this.date = date;
        this.content = content;
    }

    public String getTeacher() {
        return teacher;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getHash() {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(content.concat(date.toString()).getBytes());
            return Base64.encodeToString(messageDigest.digest(), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}