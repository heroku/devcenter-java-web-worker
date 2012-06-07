package com.heroku.devcenter;

import java.io.Serializable;

/**
 * A model class for a big, imaginary, expensive operation
 * that a user submits via the web, but is processed async
 * by a worker.
 */
public class BigOperation implements Serializable {

    private String name;

    public BigOperation() {
    }

    public BigOperation(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BigOperation{" +
                "name='" + name + '\'' +
                '}';
    }
}
