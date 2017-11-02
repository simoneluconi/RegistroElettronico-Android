package com.sharpdroid.registroelettronico.database.entities;

import android.arch.persistence.room.ColumnInfo;

public class Average {
    @ColumnInfo(name = "NAME")
    public String name = "";
    @ColumnInfo(name = "CODE")
    public int code = 0;
    @ColumnInfo(name = "AVG")
    public float avg = 0f;
    @ColumnInfo(name = "TARGET")
    public float target = 0f;
    @ColumnInfo(name = "COUNT")
    public int count = 0;

    public Average(String name, int code, float avg, int count, float target) {
        this.name = name;
        this.code = code;
        this.avg = avg;
        this.target = target;
        this.count = count;
    }

    public Average() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public float getAvg() {
        return avg;
    }

    public void setAvg(float avg) {
        this.avg = avg;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
