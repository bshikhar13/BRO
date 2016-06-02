package com.example.dexter.bro.model;

import java.io.Serializable;

/**
 * Created by Dexter on 5/31/2016.
 */
public class User implements Serializable {

    String  gid, name, email, gcmtoken;
    public User() {
    }

    public User(String gid, String name, String email, String gcmtoken) {
        this.name = name;
        this.email = email;
        this.gid = gid;
        this.gcmtoken = gcmtoken;
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

    public String getGid(){
        return this.gid;
    }

    public void setGid(String gid){
        this.gid = gid;
    }

    public String getGcmtoken(){
        return this.gcmtoken;
    }

    public void setGcmtoken(){
        this.gcmtoken = gcmtoken;
    }

}
