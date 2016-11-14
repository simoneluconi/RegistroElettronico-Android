package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

class Lesson implements Serializable {
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
}