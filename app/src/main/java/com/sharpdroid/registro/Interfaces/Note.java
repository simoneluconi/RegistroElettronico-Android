package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String teacher;
    private Date date;
    private String content;
    private String type;

    public Note(String teacher, Date date, String content, String type) {
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
}
