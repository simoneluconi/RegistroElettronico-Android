package com.sharpdroid.registro.Libray;

import java.io.Serializable;

public class Lesson implements Serializable {
    private String teacher;
    private String date;
    private String content;

    Lesson() {

    }

    public Lesson(String teacher, String date, String content) {
        this.teacher = teacher;
        this.date = date;
        this.content = content;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    String getTeacher() {
        return teacher;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String getDate() {
        return date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String getContent() {
        return content;
    }
}