package com.sharpdroid.registroelettronico.Interfaces.Client;

import android.support.annotation.IdRes;

import com.sharpdroid.registroelettronico.R;

public class HeaderEntry extends Entry {
    @IdRes
    public static int ID = R.layout.adapter_header;
    private String title;

    public HeaderEntry(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int getID() {
        return ID;
    }
}