package com.sharpdroid.registroelettronico.Interfaces.Client;

import android.text.TextUtils;

import com.sharpdroid.registroelettronico.Interfaces.API.Communication;

import java.util.Date;

public class SuperCommunication extends Communication {
    private boolean attachment;
    private String content, filename;

    public SuperCommunication(int id, String title, String content, Date date, String type, String filename, boolean attachment) {
        super(title, type, date, id);
        this.content = content;
        this.filename = filename;
        this.attachment = attachment;
    }

    public boolean isContent() {
        return !TextUtils.isEmpty(content);
    }

    public boolean isDownloaded() {
        return !TextUtils.isEmpty(filename);
    }

    public String getContent() {
        return content;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isAttachment() {
        return attachment;
    }
}
