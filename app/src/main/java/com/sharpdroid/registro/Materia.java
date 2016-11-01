package com.sharpdroid.registro;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Materia implements Serializable {
    @SerializedName("name")
    private String materia;
    @SerializedName("marks")
    private List<Voto> voti = new ArrayList<>();


    public Materia() {

    }

    Materia(String materia, List<Voto> voti) {
        this.materia = materia;
        this.voti = voti;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }


    public String getMateria() {
        return materia;
    }

    public void setVoti(List<Voto> voti) {
        this.voti = voti;
    }

    public List<Voto> getVoti() {
        return voti;
    }
}
