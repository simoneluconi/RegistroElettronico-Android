package com.sharpdroid.registroelettronico.Interfaces.Client;

import java.io.Serializable;
import java.util.List;

public class Subject implements Serializable {
    private int id, code;
    private String name;
    private float target;
    private String professor, classroom, notes;
    private List<Integer> teacherCode;

    public Subject(int id, int code, String name, float target, String professor, String classroom, String notes, List<Integer> teacherCode) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.target = target;
        this.professor = professor;
        this.classroom = classroom;
        this.notes = notes;
        this.teacherCode = teacherCode;
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
        if (getProfessors().length > 0)
            return getProfessors()[0];
        else
            return "";
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String[] getProfessors() {
        if (professor != null)
            return professor.split(",");
        else return null;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public List<Integer> getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(List<Integer> teacherCode) {
        this.teacherCode = teacherCode;
    }
}
