package com.sharpdroid.registroelettronico.Interfaces.Client;

import android.support.annotation.IdRes;

import com.sharpdroid.registroelettronico.R;

public class AgendaEntry extends Entry {
    @IdRes
    public final static int ID = R.layout.adapter_event;
    private AdvancedEvent event;

    public AgendaEntry(AdvancedEvent event) {
        this.event = event;
    }

    @Override
    public int getID() {
        return ID;
    }

    public AdvancedEvent getEvent() {
        return event;
    }
}
