package com.mp.runand.app.logic.training;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 2014-11-08.
 */
public class MessagesList {
    private List<String> messages;
    private static MessagesList instance;

    public static MessagesList getInstance() {
        if(instance == null)
            instance = new MessagesList();
        return instance;
    }

    private MessagesList() {
        this.messages = new ArrayList<String>();
        //Only to debug
        for(int i = 0; i < 3; i++) {
            messages.add("Wiadomość " + i);
        }
        messages.add("Sebastian dobrze ci idzie");
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    /**
     *
     * @param message Message to be put in list
     */
    public void putMessages(String message) {
        messages.add(message);
    }

    /**
     *
     * @return Message which is in the front of the list
     */
    public String getMessage() {
        return messages.remove(0);
    }

    public void clear() {
        messages.clear();
    }
}
