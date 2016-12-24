package com.sharpdroid.registro.Interfaces.Client;

import com.sharpdroid.registro.Interfaces.API.Delay;

import java.util.Date;

public class DelayEntry extends AbsencesEntry {
    private Delay delay;

    public DelayEntry(Delay delay) {
        this.delay = delay;
    }

    public Delay getDelay() {
        return delay;
    }

    @Override
    public Date getTime() {
        return delay.getDay();
    }
}