package com.heroku.devcenter;

import java.io.Serializable;

/**
 * @author Ryan Brainard
 */
public class BigOperation implements Serializable {

    public static final String QUEUE_NAME = "myqueue";
    
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BigOperation{" +
                "name='" + name + '\'' +
                '}';
    }
}
