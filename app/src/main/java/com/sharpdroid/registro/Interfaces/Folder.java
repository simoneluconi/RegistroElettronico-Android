package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Folder implements Serializable {
    private String name;
    private String last;
    private List<File> elements = new ArrayList<>();

    public Folder(String name, String last, List<File> elements) {
        this.name = name;
        this.last = last;
        this.elements = elements;
    }

    public String getName() {
        return name;
    }

    public String getLast() {
        return last;
    }

    public List<File> getElements() {
        return elements;
    }
}
