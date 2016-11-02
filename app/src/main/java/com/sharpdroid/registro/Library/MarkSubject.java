package com.sharpdroid.registro.Library;

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

    public void setName(String name) {
        this.name = name;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }
}
