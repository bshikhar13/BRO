package com.example.dexter.bro.model;

import java.io.Serializable;

/**
 * Created by Dexter on 5/31/2016.
 */
public class Message implements Serializable {
    String message;
    int type;

    public Message() {

    }

    public Message(String message, String createdAt, int type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public void setType(int type){
        this.type = type;
    }

    public int getType (){
        return this.type;
    }

}
