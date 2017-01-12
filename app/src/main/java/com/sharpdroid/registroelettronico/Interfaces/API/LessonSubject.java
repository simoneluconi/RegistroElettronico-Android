package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;

public class LessonSubject implements Serializable {
    private String name;
    private int code;

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
}
