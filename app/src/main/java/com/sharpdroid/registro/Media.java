package com.sharpdroid.registro;

import android.util.Log;

import java.io.Serializable;

class Media implements Serializable {
    private String materia;
    private float somma_generale;
    private float somma_orale;
    private float somma_pratico;
    private float somma_scritto;
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


    public int getNumeroVoti() {
        return this.numero_voti_generale;
    }

    public int getNumeroVotiOrale() {
        return this.numero_voti_orale;
    }

    public int getNumeroVotiPratico() {
        return this.numero_voti_pratico;
    }

    public int getNumeroVotiScritto() {
        return this.numero_voti_scritto;
    }

    public float getMediaGenerale() {
        return somma_generale / numero_voti_generale;
    }

    public float getMediaOrale() {
        return somma_orale / numero_voti_orale;
    }

    public float getMediaScritto() {
        return somma_scritto / numero_voti_scritto;
    }

    public float getMediaPratico() {
        return somma_pratico / numero_voti_pratico;
    }

    public float getSommaGenerale() {
        return somma_generale;
    }

    public float getSommaOrale() {
        return somma_orale;
    }

    public float getSommaScritto() {
        return somma_scritto;
    }

    public float getSommaPratico() {
        return somma_pratico;
    }

    public void addVoto(Voto voto) {
        if (voto.getVoto() > 0) {
            switch (voto.getTipo()) {
                case Voto.orale:
                    this.somma_orale += voto.getVoto();
                    this.numero_voti_orale++;
                    break;
                case Voto.pratico:
                    this.somma_pratico += voto.getVoto();
                    this.numero_voti_pratico++;
                    break;
                case Voto.scritto:
                    this.somma_scritto += voto.getVoto();
                    this.numero_voti_scritto++;
                    break;
            }
            this.somma_generale += voto.getVoto();
            this.numero_voti_generale++;
        } else {
            Log.e(Media.class.getCanonicalName(), "Voto inferiore a 0");
        }
    }

    private boolean isSufficiente(String tipo) {
        switch (tipo) {
            case Voto.orale:
                return getMediaOrale() > 6;
            case Voto.pratico:
                return getMediaPratico() > 6;
            case Voto.scritto:
                return getMediaScritto() > 6;
            default:
                return getMediaGenerale() > 6;
        }
    }

    boolean isSufficiente() {
        return isSufficiente("Generale");
    }
}
