package com.example.dexter.bro.model;

import java.io.Serializable;

/**
 * Created by Dexter on 5/31/2016.
 */
public class User implements Serializable {

    String  name, email;
    public User() {
    }

    public User( String name, String email) {
        this.name = name;
        this.email = email;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
