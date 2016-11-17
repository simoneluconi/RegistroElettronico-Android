package com.sharpdroid.registro.Interfaces;

import java.io.Serializable;

public class Login implements Serializable {
    private boolean ok;
    private String message;
    private String name;

    public Login(boolean ok, String message, String name) {
        this.ok = ok;
        this.message = message;
        this.name = name;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
