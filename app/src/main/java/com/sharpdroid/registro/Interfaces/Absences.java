package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.List;

public class Absences implements Serializable {
    private List<Absence> absences;
    private List<Delay> delays;
    private List<Exit> exits;

    public Absences(List<Absence> absences, List<Delay> delays, List<Exit> exits) {
        this.absences = absences;
        this.delays = delays;
        this.exits = exits;
    }

    public List<Absence> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }

    public List<Delay> getDelays() {
        return delays;
    }

    public void setDelays(List<Delay> delays) {
        this.delays = delays;
    }

    public List<Exit> getExits() {
        return exits;
    }

    public void setExits(List<Exit> exits) {
        this.exits = exits;
    }
}
