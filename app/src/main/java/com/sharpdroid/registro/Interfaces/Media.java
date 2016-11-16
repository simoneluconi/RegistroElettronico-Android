package com.sharpdroid.registro.Interfaces;

import android.util.Log;

import com.sharpdroid.registro.API.RESTFulAPI;
import com.sharpdroid.registro.R;

import java.io.Serializable;

public class Media implements Serializable {
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

    public void addMark(Mark mark) {
        if (mark.getMark() > 0) {
            switch (mark.getType()) {
                case RESTFulAPI.ORALE:
                    this.somma_orale += mark.getMark();
                    this.numero_voti_orale++;
                    break;
                case RESTFulAPI.PRATICO:
                    this.somma_pratico += mark.getMark();
                    this.numero_voti_pratico++;
                    break;
                case RESTFulAPI.SCRITTO:
                    this.somma_scritto += mark.getMark();
                    this.numero_voti_scritto++;
                    break;
            }
            this.somma_generale += mark.getMark();
            this.numero_voti_generale++;
        } else {
            Log.e(Media.class.getCanonicalName(), "Voto inferiore a 0");
        }
    }

    public boolean isSufficiente(String tipo) {
        switch (tipo) {
            case RESTFulAPI.ORALE:
                return getMediaOrale() > 6;
            case RESTFulAPI.PRATICO:
                return getMediaPratico() > 6;
            case RESTFulAPI.SCRITTO:
                return getMediaScritto() > 6;
            default:
                return getMediaGenerale() > 6;
        }
    }

    public int getColoreVoti(String tipo, float obbiettivo_voto) {
        switch (tipo) {
            case RESTFulAPI.ORALE:
                if (getMediaOrale() < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (getMediaOrale() < 5)
                    return R.color.redmaterial;
                else if (getMediaOrale() >= 5 && getMediaOrale() < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
            case RESTFulAPI.PRATICO:
                if (getMediaPratico() < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (getMediaPratico() < 5)
                    return R.color.redmaterial;
                else if (getMediaPratico() >= 5 && getMediaPratico() < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
            case RESTFulAPI.SCRITTO:
                if (getMediaScritto() < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (getMediaScritto() < 5)
                    return R.color.redmaterial;
                else if (getMediaScritto() >= 5 && getMediaScritto() < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
            default:
                if (getMediaGenerale() < obbiettivo_voto)
                    return R.color.lightgreenmaterial;
                else if (getMediaGenerale() < 5)
                    return R.color.redmaterial;
                else if (getMediaGenerale() >= 5 && getMediaGenerale() < 6)
                    return R.color.orangematerial;
                else
                    return R.color.greenmaterial;
        }
    }

    public boolean isSufficiente() {
        return isSufficiente("Generale");
    }

    public int getMediaColor(float obbiettivo_voto) {
        return getColoreVoti("Generale", obbiettivo_voto);
    }
}
