package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MarkSubject implements Serializable {
    private String name;
    private List<Mark> marks = new ArrayList<>();

    public MarkSubject(String name, List<Mark> marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() {
        return name.substring(0, 1).toUpperCase(Locale.getDefault()) + name.substring(1);
    }

    public List<Mark> getMarks() {
        return marks;
    }
}
