package com.sharpdroid.registroelettronico.Interfaces.Client;

public class Average {
    public final String name;
    public final int code;
    public final float avg;
    public final float target;
    public final int count;

    public Average(String name, int code, float avg, int count, float target) {
        this.name = name;
        this.code = code;
        this.avg = avg;
        this.count = count;
        this.target = target;
    }
}
