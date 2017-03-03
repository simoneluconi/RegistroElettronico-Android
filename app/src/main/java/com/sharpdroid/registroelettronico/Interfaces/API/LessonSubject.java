package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;
import java.util.List;

public class LessonSubject implements Serializable {
    private String name;
    private int code;
    private List<Integer> teacherCodes;

    public LessonSubject(String name, int code, List<Integer> teacherCodes) {
        this.name = name;
        this.code = code;
        this.teacherCodes = teacherCodes;
    }

    public List<Integer> getTeacherCodes() {
        return teacherCodes;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }
}
