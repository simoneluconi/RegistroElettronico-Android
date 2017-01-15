package com.sharpdroid.registroelettronico.Interfaces.Client;

import android.util.Log;

import com.sharpdroid.registroelettronico.API.SpiaggiariAPI;
import com.sharpdroid.registroelettronico.Interfaces.API.Mark;

import java.io.Serializable;
import java.util.List;

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

    public float getMediaGenerale() throws ArithmeticException {
        if (numero_voti_generale == 0) throw new ArithmeticException("Divisore uguale a 0");
        return somma_generale / numero_voti_generale;
    }

    public float getMediaOrale() throws ArithmeticException {
        if (numero_voti_orale == 0) throw new ArithmeticException("Divisore uguale a 0");
        return somma_orale / numero_voti_orale;
    }

    public float getMediaScritto() throws ArithmeticException {
        if (numero_voti_scritto == 0) throw new ArithmeticException("Divisore uguale a 0");
        return somma_scritto / numero_voti_scritto;
    }

    public float getMediaPratico() throws ArithmeticException {
        if (numero_voti_pratico == 0) throw new ArithmeticException("Divisore uguale a 0");
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

    public void addMarks(List<Mark> marks) {
        for (Mark mark : marks)
            addMark(mark);
    }

    public void addMark(Mark mark) {
        if (!mark.isNs()) {
            if (isNumericMark(mark))
                if (Float.parseFloat(mark.getMark()) > 0) {
                    switch (mark.getType()) {
                        case SpiaggiariAPI.ORALE:
                            this.somma_orale += Float.parseFloat(mark.getMark());
                            this.numero_voti_orale++;
                            break;
                        case SpiaggiariAPI.PRATICO:
                            this.somma_pratico += Float.parseFloat(mark.getMark());
                            this.numero_voti_pratico++;
                            break;
                        case SpiaggiariAPI.SCRITTO:
                            this.somma_scritto += Float.parseFloat(mark.getMark());
                            this.numero_voti_scritto++;
                            break;
                    }
                    this.somma_generale += Float.parseFloat(mark.getMark());
                    this.numero_voti_generale++;
                } else {
                    Log.e(Media.class.getCanonicalName(), String.format("Voto inferiore a 0 -> %1$s", mark.getMark()));
                }
        } else {
            Log.d(Media.class.getCanonicalName(), String.format("%s %s non è significativo", materia, mark.getMark()));
        }
    }

    private boolean isNumericMark(Mark mark) {
        try {
            Float.parseFloat(mark.getMark());
        } catch (java.lang.NumberFormatException ex) {
            Log.d("isNumericMark", String.format("Voto non valido: %s ", mark.getMark()));
            mark.setNs(true);
            return false;
        }
        return true;
    }

    public boolean containsValidMarks() {
        return (numero_voti_generale > 0);
    }
}