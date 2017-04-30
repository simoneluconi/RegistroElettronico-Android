package com.sharpdroid.registroelettronico.Interfaces.API;

import android.util.Base64;

import com.sharpdroid.registroelettronico.Interfaces.Client.FileElement;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Folder extends FileElement implements Serializable {
    private String profName;
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

    public String getProfName() {
        return profName;
    }

    public void setProfName(String profName) {
        this.profName = profName;
    }

    public String getHash() {
        try {
            return Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(profName.concat(name).getBytes()), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
