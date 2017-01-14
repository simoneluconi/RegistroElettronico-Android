package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;
import java.util.Date;

public class Mark implements Serializable {
    public static final String PRIMO_PERIODO = "q1";
    public static final String SECONDO_PERIODO = "q3";
    private String q;
    private boolean ns;
    private String type;
    private Date date;
    private String mark;
    private String desc;


    Mark(String q, boolean ns, String type, Date date, String mark, String desc) {
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

    public void setNs(boolean isNs) {
        ns = isNs;
    }

    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getMark() {
        return mark;
    }

    public String getDesc() {
        return desc;
    }
}
