package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;

import static com.sharpdroid.registroelettronico.Utils.Metodi.contactTeachersCodes;

public class LessonSubject implements Serializable {
    private String name;
    private int code;
    private int[] teacherCodes;

    public LessonSubject(String name, int code, int[] teacherCodes) {
        this.name = name;
        this.code = code;
        this.teacherCodes = teacherCodes;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public int[] getTeacherCodes() {
        return teacherCodes;
    }

    public String getTeacherCodeString()
    {
        return contactTeachersCodes(teacherCodes);
    }
}
