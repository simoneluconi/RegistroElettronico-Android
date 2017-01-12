package com.sharpdroid.registroelettronico.Interfaces.API;

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

    public List<Delay> getDelays() {
        return delays;
    }

    public List<Exit> getExits() {
        return exits;
    }
}
