package com.sharpdroid.registroelettronico.Interfaces.Client;

import com.sharpdroid.registroelettronico.Interfaces.API.Event;

import java.util.Date;

public class AdvancedEvent extends Event {
    private long completed;

    public AdvancedEvent(String id, String title, Date start, Date end, boolean allDay, Date data_inserimento, String nota_2, String master_id, String classe_id, String classe_desc, int gruppo, String autore_desc, String autore_id, String tipo, String materia_desc, String materia_id, long completed) {
        super(id, title, start, end, allDay, data_inserimento, nota_2, master_id, classe_id, classe_desc, gruppo, autore_desc, autore_id, tipo, materia_desc, materia_id);
    }

    public long isCompleted() {
        return completed;
    }
}
