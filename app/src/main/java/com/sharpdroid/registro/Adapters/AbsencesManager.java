package com.sharpdroid.registro.Adapters;

import com.sharpdroid.registro.Interfaces.Absence;
import com.sharpdroid.registro.Interfaces.Absences;
import com.sharpdroid.registro.Interfaces.Delay;
import com.sharpdroid.registro.Interfaces.Exit;

import java.util.ArrayList;
import java.util.List;

public class AbsencesManager {
    private Absences absences;

    public AbsencesManager(Absences absences) {
        this.absences.set(absences);
    }

    public void set(Absences absences) {
        this.absences.set(absences);
    }

    public void clear() {
        absences.clear();
    }

    public List<Absence> getAbsences() {
        return new ArrayList<>(absences.getAbsences());
    }

    public List<Delay> getDelays() {
        return new ArrayList<>(absences.getDelays());
    }

    public List<Exit> getExits() {
        return new ArrayList<>(absences.getExits());
    }


}
