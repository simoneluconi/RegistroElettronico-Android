package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;

public class LessonSubject implements Serializable {
    private String name;
    private int code;
    private String professor;

    public LessonSubject(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getProfessor() {
        return professor;
    }
}
