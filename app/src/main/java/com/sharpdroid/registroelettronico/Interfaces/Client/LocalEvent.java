package com.sharpdroid.registroelettronico.Interfaces.Client;

import java.util.Date;

public class LocalEvent {
    private String uuid;
    private String title;
    private String content;
    private String type;
    private Date day;
    private int subject_id;
    private int prof_id;
    private Date completed_at;

    public LocalEvent(String uuid, String title, String content, String type, Date day, int subject_id, int prof_id, Date completed_at) {
        this.uuid = uuid;
        this.title = title;
        this.content = content;
        this.type = type;
        this.day = day;
        this.subject_id = subject_id;
        this.prof_id = prof_id;
        this.completed_at = completed_at;
    }

    public String getUuid() {
        return uuid;
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

    public int getSubjectId() {
        return subject_id;
    }

    public int getProfId() {
        return prof_id;
    }

    public Date getCompletedAt() {
        return completed_at;
    }
}
