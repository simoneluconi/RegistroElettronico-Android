package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Folder implements Serializable {
    private String name;
    private Date last;
    private List<File> elements = new ArrayList<>();

    public Folder(String name, Date last, List<File> elements) {
        this.name = name;
        this.last = last;
        this.elements = elements;
    }

    public String getName() {
        return name;
    }

    public Date getLast() {
        return last;
    }

    public List<File> getElements() {
        return elements;
    }
}
