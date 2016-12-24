package com.sharpdroid.registro.Interfaces.Client;

import com.sharpdroid.registro.Interfaces.API.Absence;

import java.util.Date;

public class AbsenceEntry extends AbsencesEntry {
    private Absence absence;

    public AbsenceEntry(Absence absence) {
        this.absence = absence;
    }

    public Absence getAbsence() {
        return absence;
    }

    @Override
    public Date getTime() {
        return absence.getFrom();
    }
}