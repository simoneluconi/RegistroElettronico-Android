package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Absences implements Serializable {
    private List<Absence> absences = new ArrayList<>();
    private List<Delay> delays = new ArrayList<>();
    private List<Exit> exits = new ArrayList<>();

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
