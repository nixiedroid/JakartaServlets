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
    private boolean has_milk;
    private Timestamp created;

    public Coffee() {
    }

    public Coffee(String name,boolean has_milk) {
        this.name = name;
        this.has_milk = has_milk;
    }

    public Coffee(int id, String name, boolean has_milk, Timestamp created) {
        this.created = created;
        this.name = name;
        this.has_milk = has_milk;
        this.id = id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public boolean isHas_milk() {
        return has_milk;
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
