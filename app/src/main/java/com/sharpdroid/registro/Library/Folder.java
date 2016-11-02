package com.sharpdroid.registro.Library;

import java.util.List;

class Folder {
    private String name;
    private String last;
    private List<File> elements;

    public Folder(String name, String last, List<File> elements) {
        this.name = name;
        this.last = last;
        this.elements = elements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public List<File> getElements() {
        return elements;
    }

    public void setElements(List<File> elements) {
        this.elements = elements;
    }
}
