package com.sharpdroid.registro.Interfaces.Client;

import com.sharpdroid.registro.Interfaces.API.Absence;

public class AbsenceEntry extends AbsencesEntry {
    private Absence absence;

    public AbsenceEntry(Absence absence) {
        this.absence = absence;
    }

    public Absence getAbsence() {
        return absence;
    }
}