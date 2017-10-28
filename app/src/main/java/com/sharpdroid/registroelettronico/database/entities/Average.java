package com.sharpdroid.registroelettronico.database.entities;

import com.orm.dsl.Column;

public class Average {
    @Column(name = "NAME")
    public String name = "";
    @Column(name = "CODE")
    public int code = 0;
    @Column(name = "AVG")
    public float avg = 0f;
    @Column(name = "TARGET")
    public float target = 0f;
    @Column(name = "COUNT")
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
