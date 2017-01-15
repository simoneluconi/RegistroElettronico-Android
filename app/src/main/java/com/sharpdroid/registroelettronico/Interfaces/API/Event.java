package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;
import java.util.Date;

public class Event implements Serializable {
    private String id;
    private String title;
    private Date start;
    private Date end;
    private boolean allDay;
    private Date data_inserimento;
    private String nota_2;
    private String master_id;
    private String classe_id;
    private String classe_desc;
    private int gruppo;
    private String autore_desc;
    private String autore_id;
    private String tipo;
    private String materia_desc;
    private String materia_id;

    public Event(String id, String title, Date start, Date end, boolean allDay, Date data_inserimento, String nota_2, String master_id, String classe_id, String classe_desc, int gruppo, String autore_desc, String autore_id, String tipo, String materia_desc, String materia_id) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
        this.data_inserimento = data_inserimento;
        this.nota_2 = nota_2;
        this.master_id = master_id;
        this.classe_id = classe_id;
        this.classe_desc = classe_desc;
        this.gruppo = gruppo;
        this.autore_desc = autore_desc;
        this.autore_id = autore_id;
        this.tipo = tipo;
        this.materia_desc = materia_desc;
        this.materia_id = materia_id;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public Date getData_inserimento() {
        return data_inserimento;
    }

    public String getNota_2() {
        return nota_2;
    }

    public String getMaster_id() {
        return master_id;
    }

    public String getClasse_id() {
        return classe_id;
    }

    public String getClasse_desc() {
        return classe_desc;
    }

    public int getGruppo() {
        return gruppo;
    }

    public String getAutore_desc() {
        return autore_desc;
    }

    public String getAutore_id() {
        return autore_id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMateria_desc() {
        return materia_desc;
    }

    public String getMateria_id() {
        return materia_id;
    }
}
