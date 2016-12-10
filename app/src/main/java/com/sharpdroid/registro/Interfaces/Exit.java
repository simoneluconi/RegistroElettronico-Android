package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Exit implements Serializable {
    private boolean done;

    public Exit(boolean done) {
        this.done = done;
    }

    public boolean isDone() {
        return done;
    }
}
