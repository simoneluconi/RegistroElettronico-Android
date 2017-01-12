package com.sharpdroid.registroelettronico.Interfaces.Client;

import android.support.annotation.IdRes;

import com.sharpdroid.registroelettronico.Interfaces.API.Event;
import com.sharpdroid.registroelettronico.R;

public class AgendaEntry extends Entry {
    @IdRes
    public final static int ID = R.layout.adapter_event;
    private Event event;

    public AgendaEntry(Event event) {
        this.event = event;
    }

    @Override
    public int getID() {
        return ID;
    }

    public Event getEvent() {
        return event;
    }
}
