package com.sharpdroid.registro.Libray;

import java.io.Serializable;

public class Mark implements Serializable {
    private String q;
    private boolean ns;
    private String type;
    private String date;
    private float mark;
    private String desc;

    public Mark() {

    }

    Mark(String q, boolean ns, String type, String date, float mark, String desc) {
        this.q = q;
        this.ns = ns;
        this.type = type;
        this.date = date;
        this.mark = mark;
        this.desc = desc;
    }

    String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    boolean isNS() {
        return ns;
    }

    public void setNS(boolean ns) {
        this.ns = ns;
    }

    String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    float getMark() {
        return mark;
    }

    public void setMark(float mark) {
        this.mark = mark;
    }

    String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isSufficiente() {
        return this.mark > 6;
    }
}
