package com.sharpdroid.registroelettronico.Interfaces.API;

import com.sharpdroid.registroelettronico.Interfaces.Client.FileElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileTeacher extends FileElement implements Serializable {
    private String name;
    private List<Folder> folders = new ArrayList<>();

    public FileTeacher(String name, List<Folder> folders) {
        this.name = name;
        this.folders = folders;
    }

    public String getName() {
        return name;
    }

    public List<Folder> getFolders() {
        return folders;
    }
}
