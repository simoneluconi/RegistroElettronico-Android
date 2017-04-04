package com.sharpdroid.registroelettronico.Interfaces.API;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MarkSubject implements Serializable {
    private String name;
    private List<Mark> marks = new ArrayList<>();

    public MarkSubject(String name, List<Mark> marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks.clear();
        this.marks.addAll(marks);
    }
}
