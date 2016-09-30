package com.sharpdroid.registro;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

class Voto implements Serializable {
    @SerializedName("q")
    private String q;
    @SerializedName("ns")
    private boolean blu;
    @SerializedName("type")
    private String tipo;
    @SerializedName("date")
    private String data;
    @SerializedName("mark")
    private double voto;
    @SerializedName("desc")
    private String commento;
    @SerializedName("materia")
    private String materia;

    static final String orale = "Orale";
    static final String scritto = "Scritto";
    static final String pratico = "Pratico";

    public Voto() {

    }

    Voto(String materia, String tipo, String data, String commento, String q, boolean blu, double voto) {
        this.materia = materia;
        this.tipo = tipo;
        this.data = data;
        this.commento = commento;
        this.q = q;
        this.blu = blu;
        this.voto = voto;
    }

    String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    String getTipo() {
        return tipo;
    }

    public void setTipo(String materia) {
        this.tipo = tipo;
    }

    String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    String getCommento() {
        return commento;
    }

    public void setCommento(String commento) {
        this.materia = commento;
    }

    String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    boolean isBlu() {
        return blu;
    }

    public void setBlu(boolean blu) {
        this.blu = blu;
    }

    double getVoto() {
        return voto;
    }

    public void setVoto(double voto) {
        this.voto = voto;
    }

    boolean isSufficiente() {
        return this.voto > 6;
    }
}

