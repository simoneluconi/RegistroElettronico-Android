package com.sharpdroid.registroelettronico.Interfaces.API;

import android.util.Base64;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Note implements Serializable {
    private String teacher;
    private Date date;
    private String content;
    private String type;

    public Note(String teacher, String content, Date date, String type) {
        this.teacher = teacher;
        this.date = date;
        this.content = content;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public String getHash() {
        try {
            return Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(type.concat(content).concat(date.toString()).concat(teacher).getBytes()), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
