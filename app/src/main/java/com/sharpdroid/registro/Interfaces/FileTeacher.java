package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;
import java.util.List;

class FileTeacher implements Serializable {
    private String name;
    private List<Folder> folders;

    public FileTeacher(String name, List<Folder> folders) {
        this.name = name;
        this.folders = folders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public void setFolders(List<Folder> folders) {
        this.folders = folders;
    }
}
