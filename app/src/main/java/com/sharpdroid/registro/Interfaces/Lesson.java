package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Lesson implements Serializable {
    private String subject;
    private String teacher;
    private String date;
    private String content;

    public Lesson(String subject, String teacher, String date, String content) {
        this.subject = subject;
        this.teacher = teacher;
        this.date = date;
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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