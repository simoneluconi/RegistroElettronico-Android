package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class AllAbsences implements Serializable {
    private Absences undone;
    private Absences done;

    public AllAbsences(Absences undone, Absences done) {
        this.undone = undone;
        this.done = done;
    }

    public Absences getUndone() {
        return undone;
    }

    public void setUndone(Absences undone) {
        this.undone = undone;
    }

    public Absences getDone() {
        return done;
    }

    public void setDone(Absences done) {
        this.done = done;
    }
}
