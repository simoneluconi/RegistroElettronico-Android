package com.sharpdroid.registro.Interfaces.Client;

import com.sharpdroid.registro.Interfaces.API.Exit;

public class ExitEntry extends AbsencesEntry {
    private Exit exit;

    public ExitEntry(Exit exit) {
        this.exit = exit;
    }

    public Exit getExit() {
        return exit;
    }
}
