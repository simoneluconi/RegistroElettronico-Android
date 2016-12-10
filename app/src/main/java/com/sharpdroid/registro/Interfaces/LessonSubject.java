package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class LessonSubject implements Serializable {
    private String name;
    private int code;

    public LessonSubject(String name, int code) {
        this.name = name;
        this.code = code;
    }

    String getName() {
        return name;
    }

    int getCode() {
        return code;
    }
}
