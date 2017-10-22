package com.sharpdroid.registroelettronico.Interfaces.Client;

import com.orm.SugarRecord;
import com.sharpdroid.registroelettronico.Databases.Entities.Teacher;

import java.util.Date;

public class LocalEvent extends SugarRecord {
    private String title;
    private String content;
    private String type;
    private Date day;
    private com.sharpdroid.registroelettronico.Databases.Entities.Subject subject_id;
    private Teacher prof_id;
    private Date completed_at;

    public LocalEvent(String title, String content, String type, Date day, com.sharpdroid.registroelettronico.Databases.Entities.Subject subject_id, Teacher prof_id, Date completed_at) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.day = day;
        this.subject_id = subject_id;
        this.prof_id = prof_id;
        this.completed_at = completed_at;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public Date getDay() {
        return day;
    }

    public com.sharpdroid.registroelettronico.Databases.Entities.Subject getSubject() {
        return subject_id;
    }

    public Teacher getTeacher() {
        return prof_id;
    }

    public Date getCompletedAt() {
        return completed_at;
    }
}
