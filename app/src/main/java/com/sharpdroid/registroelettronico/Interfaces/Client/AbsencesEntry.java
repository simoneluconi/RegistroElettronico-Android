package com.sharpdroid.registroelettronico.Interfaces.Client;

import com.sharpdroid.registroelettronico.R;

import java.util.Date;

public abstract class AbsencesEntry extends Entry {

    @Override
    public int getID() {
        return R.layout.adapter_absence;
    }

    public abstract Date getTime();
}
