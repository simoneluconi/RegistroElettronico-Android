package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
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
}