package com.mp.runand.app.logic.entities;

/**
 * Trainer which can be added by user
 * Created by Mateusz on 2014-11-16.
 */
public class Trainer {

    private String email;
    private long id;

    public Trainer(String email, long id){
        this.email = email;
        this.id = id;
    }

    @Override
    public String toString(){
        return id+". "+email;
    }

    public long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }
}
