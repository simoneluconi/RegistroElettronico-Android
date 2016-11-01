package com.sharpdroid.registro;

import android.util.Log;

import java.io.Serializable;

class Media implements Serializable {
    private String materia;
    private double media_generale;
    private double media_orale;
    private double media_pratico;
    private double media_scritto;
    private int numero_voti_generale;
    private int numero_voti_orale;
    private int numero_voti_pratico;
    private int numero_voti_scritto;

    public Media() {

    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }

    public double getMediaGenerale() {
        return media_generale / numero_voti_generale;
    }

    public double getNumeroVoti() {
        return this.numero_voti_generale;
    }

    public double getMediaOrale() {
        return media_orale / numero_voti_orale;
    }

    public double getMediaScritto() {
        return media_scritto / numero_voti_scritto;
    }

    public double getMediaPratico() {
        return media_pratico / numero_voti_pratico;
    }

    public void addVoto(Voto voto) {
        if (voto.getVoto() > 0) {
            switch (voto.getTipo()) {
                case Voto.orale:
                    this.media_orale += voto.getVoto();
                    this.numero_voti_orale++;
                    break;
                case Voto.pratico:
                    this.media_pratico += voto.getVoto();
                    this.numero_voti_pratico++;
                    break;
                case Voto.scritto:
                    this.media_scritto += voto.getVoto();
                    this.numero_voti_scritto++;
                    break;
            }
            this.media_generale += voto.getVoto();
            this.numero_voti_generale++;
        } else {
            Log.e(Media.class.getCanonicalName(), "Voto inferiore a 0");
        }
    }

    public boolean isSufficiente(String tipo) {
        switch (tipo) {
            case Voto.orale:
                return this.media_orale > 6;
            case Voto.pratico:
                return this.media_pratico > 6;
            case Voto.scritto:
                return this.media_scritto > 6;
            default:
                return this.media_generale > 6;
        }
    }

    boolean isSufficiente() {
        return isSufficiente("Generale");
    }
}
