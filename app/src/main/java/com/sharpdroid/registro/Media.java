package com.sharpdroid.registro;

import android.util.Log;

import java.io.Serializable;

class Media implements Serializable {
    private String materia;
    private float media_generale;
    private float media_orale;
    private float media_pratico;
    private float media_scritto;
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

    public float getMediaGenerale() {
        return media_generale / numero_voti_generale;
    }

    public float getNumeroVoti() {
        return this.numero_voti_generale;
    }

    public float getMediaOrale() {
        return media_orale / numero_voti_orale;
    }

    public float getMediaScritto() {
        return media_scritto / numero_voti_scritto;
    }

    public float getMediaPratico() {
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

    private boolean isSufficiente(String tipo) {
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
