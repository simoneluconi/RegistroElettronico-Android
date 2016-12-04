package com.sharpdroid.registro.Interfaces;

public class Subject {

    private int id, code;
    private String name;
    private float target;
    private String professor, classroom, notes;

    public Subject(int id, int code, String name, float target, String professor, String classroom, String notes) {
        this.id = id;
        this.code = code;
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
}
