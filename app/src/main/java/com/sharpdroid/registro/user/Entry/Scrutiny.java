package com.sharpdroid.registro.user.Entry;

import java.io.Serializable;

public class Scrutiny implements Serializable {
    private String icon;
    private String desc;
    private String type;
    private String link;
    private String sess;
    private boolean downloadable;

    public Scrutiny(String icon, String desc, String type, String link, String sess, boolean downloadable) {
        this.icon = icon;
        this.desc = desc;
        this.type = type;
        this.link = link;
        this.sess = sess;
        this.downloadable = downloadable;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSess() {
        return sess;
    }

    public void setSess(String sess) {
        this.sess = sess;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
