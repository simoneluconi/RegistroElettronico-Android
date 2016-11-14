package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Mark implements Serializable {
    private String q;
    private boolean ns;
    private String type;
    private String date;
    private float mark;
    private String desc;

    Mark(String q, boolean ns, String type, String date, float mark, String desc) {
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

    public void setQ(String q) {
        this.q = q;
    }

    public boolean isNs() {
        return ns;
    }

    public void setNs(boolean ns) {
        this.ns = ns;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getMark() {
        return mark;
    }

    public void setMark(float mark) {
        this.mark = mark;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSufficiente() {
        return this.mark > 6;
    }
}
