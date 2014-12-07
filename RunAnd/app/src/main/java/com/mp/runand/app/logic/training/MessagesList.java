package com.mp.runand.app.logic.training;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 2014-11-08.
 */
public class MessagesList {
    private List<String> messages;
    private static MessagesList instance;
    private String lastMessage;

    public static MessagesList getInstance() {
        if(instance == null) {
            instance = new MessagesList();
            Log.e("ML", "Tworzenie instancji");
        }
        return instance;
    }

    private MessagesList() {
        this.messages = new ArrayList<String>();
        messages.add("DUPA");
        lastMessage="";
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    /**
     *
     * @param message Message to be put in list
     */
    public void putMessages(String message) {
        if(!message.equals(lastMessage)) {
            messages.add(message);
            lastMessage = message;
        }
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
