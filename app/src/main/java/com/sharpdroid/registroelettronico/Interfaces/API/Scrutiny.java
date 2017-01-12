package com.sharpdroid.registroelettronico.Interfaces.API;

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

    public String getDesc() {
        return desc;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public String getSess() {
        return sess;
    }

    public boolean isDownloadable() {
        return downloadable;
    }
}
