package com.example.dexter.bro.app;

/**
 * Created by Dexter on 5/31/2016.
 */
public class Endpoints {
    public static final String BASE_URL = "http://192.168.1.3/brochat/v1";
    public static final String LOGIN = BASE_URL + "/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}
