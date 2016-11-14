package com.sharpdroid.registro.user.Entry;

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

    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isAttachment() {
        return attachment;
    }

    public void setAttachment(boolean attachment) {
        this.attachment = attachment;
    }
}
