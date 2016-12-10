package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class CommunicationDescription implements Serializable {
    private String longTitle;
    private String desc;
    private boolean attachment;

    public CommunicationDescription(String longTitle, String desc, boolean attachment) {
        this.longTitle = longTitle;
        this.desc = desc;
        this.attachment = attachment;
    }

    public String getLongTitle() {
        return longTitle;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isAttachment() {
        return attachment;
    }
}
