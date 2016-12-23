package com.sharpdroid.registro.Interfaces.API;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.sharpdroid.registro.Utils.Metodi.beautifyName;

public class MarkSubject implements Serializable {
    private String name;
    private List<Mark> marks = new ArrayList<>();

    public MarkSubject(String name, List<Mark> marks) {
        this.name = name;
        this.marks = marks;
    }

    public String getName() {
        return beautifyName(name);
    }

    public List<Mark> getMarks() {
        return marks;
    }
}
