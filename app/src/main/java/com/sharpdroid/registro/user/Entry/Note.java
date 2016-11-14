package com.sharpdroid.registro.user.Entry;

import java.io.Serializable;

public class Note implements Serializable {
    private String teacher;
    private String date;
    private String content;
    private String type;

    public Note(String teacher, String date, String content, String type) {
        this.teacher = teacher;
        this.date = date;
        this.content = content;
        this.type = type;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
