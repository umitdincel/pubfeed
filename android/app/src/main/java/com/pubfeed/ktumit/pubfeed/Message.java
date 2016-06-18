package com.pubfeed.ktumit.pubfeed;

/**
 * Created by ktumit on 18/05/16.
 */
public class Message {

    String message;
    String name;
    String avatar;

    public Message(String name, String message, String avatar) {
        this.message = message;
        this.name = name;
        this.avatar = avatar;
    }
}
