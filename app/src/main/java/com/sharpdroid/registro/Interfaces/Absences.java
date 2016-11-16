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

    public void setData(Absences a){
        absences.addAll(a.getAbsences());
        delays.addAll(a.getDelays());
        exits.addAll(a.getExits());
    }
    public void clear(){
        absences.clear();
        delays.clear();
        exits.clear();
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

    /**
     * @param i 0: absences 1: delays 2: exits
     * @return Size of list
     */
    public int getSize(int i) {
        switch (i) {
            case 0:
                return absences.size();
            case 1:
                return delays.size();
            case 2:
                return exits.size();
        }
        return 0;
    }

    /**
     * @param i 0: absences 1: delays 2: exits
     * @return Data of list
     */
    public List getGroup(int i) {
        switch (i) {
            case 0:
                return absences;
            case 1:
                return delays;
            case 2:
                return exits;
            default:
                return null;
        }
    }
}
