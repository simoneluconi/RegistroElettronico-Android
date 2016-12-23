package com.sharpdroid.registro.Interfaces.Client;


import android.support.annotation.IdRes;

import com.sharpdroid.registro.R;

public abstract class AbsencesEntry extends Entry {
    @IdRes
    public static int ID = R.layout.adapter_absence;

    @Override
    public int getID() {
        return ID;
    }
}
