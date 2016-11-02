package com.sharpdroid.registro.Library;

import java.io.Serializable;

class LessonSubject implements Serializable {
    private String name;
    private int code;

    public LessonSubject(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    public void setCode(int code) {
        this.code = code;
    }

    int getCode() {
        return code;
    }
}