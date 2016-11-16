package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.List;

public class Absences implements Serializable {
    private String done;
    private List<Absence> absences_done;
    private List<Delay> delays_done;
    //private List<Exit> exits_done;
    private String undone;
    private List<Absence> absences_undone;
    private List<Delay> delays_undone;
    //private List<Exit> exits_undone;

    public Absences(String done, List<Absence> absences_done, List<Delay> delays_done, String undone, List<Absence> absences_undone, List<Delay> delays_undone) {
        this.done = done;
        this.absences_done = absences_done;
        this.delays_done = delays_done;
        this.undone = undone;
        this.absences_undone = absences_undone;
        this.delays_undone = delays_undone;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public List<Absence> getAbsences_done() {
        return absences_done;
    }

    public void setAbsences_done(List<Absence> absences_done) {
        this.absences_done = absences_done;
    }

    public List<Delay> getDelays_done() {
        return delays_done;
    }

    public void setDelays_done(List<Delay> delays_done) {
        this.delays_done = delays_done;
    }

    public String getUndone() {
        return undone;
    }

    public void setUndone(String undone) {
        this.undone = undone;
    }

    public List<Absence> getAbsences_undone() {
        return absences_undone;
    }

    public void setAbsences_undone(List<Absence> absences_undone) {
        this.absences_undone = absences_undone;
    }

    public List<Delay> getDelays_undone() {
        return delays_undone;
    }

    public void setDelays_undone(List<Delay> delays_undone) {
        this.delays_undone = delays_undone;
    }
}
