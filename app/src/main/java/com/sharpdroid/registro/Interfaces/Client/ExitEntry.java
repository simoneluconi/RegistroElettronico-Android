package com.sharpdroid.registro.Interfaces.Client;

import com.sharpdroid.registro.Interfaces.API.Exit;

import java.util.Date;

public class ExitEntry extends AbsencesEntry {
    private Exit exit;

    public ExitEntry(Exit exit) {
        this.exit = exit;
    }

    public Exit getExit() {
        return exit;
    }

    @Override
    public Date getTime() {
        return exit.getDay();
    }
}
