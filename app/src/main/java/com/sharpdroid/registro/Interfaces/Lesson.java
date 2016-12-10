package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Lesson implements Serializable {
    private String teacher;
    private String date;
    private String content;

    public Lesson(String teacher, String date, String content) {
        this.teacher = teacher;
        this.date = date;
        this.content = content;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}