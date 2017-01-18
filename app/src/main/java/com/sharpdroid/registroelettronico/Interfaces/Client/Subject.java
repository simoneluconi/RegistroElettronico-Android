package com.sharpdroid.registroelettronico.Interfaces.Client;

import java.io.Serializable;

public class Subject implements Serializable {
    private int id, code;
    private String original_name, name;
    private float target;
    private String professor, classroom, notes;

    public Subject(int id, int code, String original_name, String name, float target, String professor, String classroom, String notes) {
        this.id = id;
        this.code = code;
        this.original_name = original_name;
        this.name = name;
        this.target = target;
        this.professor = professor;
        this.classroom = classroom;
        this.notes = notes;
    }

    public String getClassroom() {
        return classroom;
    }

    public int getCode() {
        return code;
    }

    public int getId() {
        return id;
    }

    public String getOriginalName() {
        return original_name;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public String getProfessor() {
        return professor;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void setProfessor(String professor)
    {
        this.professor = professor;
    }
}
