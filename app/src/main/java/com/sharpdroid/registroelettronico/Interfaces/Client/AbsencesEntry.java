package com.sharpdroid.registroelettronico.Interfaces.Client;


import android.support.annotation.IdRes;

import com.sharpdroid.registroelettronico.R;

import java.util.Date;

public abstract class AbsencesEntry extends Entry {
    @IdRes
    public static int ID = R.layout.adapter_absence;

    @Override
    public int getID() {
        return ID;
    }

    public abstract Date getTime();
}
