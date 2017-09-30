package com.sharpdroid.registroelettronico.Interfaces.Client;

import android.support.annotation.LayoutRes;

import com.sharpdroid.registroelettronico.Databases.Entities.SuperAgenda;
import com.sharpdroid.registroelettronico.R;

public class AgendaEntry extends Entry {
    @LayoutRes
    private final static int ID = R.layout.adapter_event;
    private SuperAgenda event;

    public AgendaEntry(SuperAgenda event) {
        this.event = event;
    }

    @Override
    public int getID() {
        return ID;
    }

    public SuperAgenda getEvent() {
        return event;
    }
}
