package com.sharpdroid.registro.Interfaces;

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

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }
}
