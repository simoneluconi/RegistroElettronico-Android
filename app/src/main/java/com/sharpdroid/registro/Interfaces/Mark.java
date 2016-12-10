package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Mark implements Serializable {
    private String q;
    private boolean ns;
    private String type;
    private String date;
    private String mark;
    private String desc;

    Mark(String q, boolean ns, String type, String date, String mark, String desc) {
        this.q = q;
        this.ns = ns;
        this.type = type;
        this.date = date;
        this.mark = mark;
        this.desc = desc;
    }

    public String getQ() {
        return q;
    }

    public boolean isNs() {
        return ns;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getMark() {
        return mark;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isSufficiente() {
        return Float.parseFloat(this.mark) > 6;
    }
}
