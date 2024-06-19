package com.nixiedroid.jakarta.rest.models;

import java.beans.JavaBean;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@JavaBean
public class Coffee implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private Timestamp created;

    public Coffee() {
    }

    public Coffee(String name) {
        this.name = name;
    }

    public Coffee(int id, String name, Timestamp created) {
        this.created = created;
        this.name = name;
        this.id = id;
    }

    public Timestamp getCreated() {
        return created;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

}
