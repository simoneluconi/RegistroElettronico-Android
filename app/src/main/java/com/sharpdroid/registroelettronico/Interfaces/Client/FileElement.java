package com.sharpdroid.registroelettronico.Interfaces.Client;

import com.sharpdroid.registroelettronico.Interfaces.API.FileTeacher;
import com.sharpdroid.registroelettronico.Interfaces.API.Folder;

import java.util.ArrayList;
import java.util.List;

public class FileElement {

    private List<FileElement> fileElements;

    public FileElement() {
        fileElements = new ArrayList<>();
    }

    public void ConvertFileTeachertoFileElement(List<FileTeacher> fileTeachers) {
        for (FileTeacher fileTeacher : fileTeachers) {
            fileElements.add(fileTeacher);
            for (Folder folder : fileTeacher.getFolders()) {
                folder.setProfName(fileTeacher.getName());
                fileElements.add(folder);
            }
        }
    }

    public FileElement get(int position) {
        return fileElements.get(position);
    }

    public void clear() {
        fileElements.clear();
    }

    public int size() {
        return fileElements.size();
    }
}
