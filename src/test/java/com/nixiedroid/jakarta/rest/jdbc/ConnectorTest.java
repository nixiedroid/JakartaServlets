package com.nixiedroid.jakarta.rest.jdbc;

import com.nixiedroid.jakarta.rest.models.Coffee;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

class ConnectorTest {
    @Test
    void test() {
        Connector con = new PostgresConnector();
        for (Coffee c : con.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
        con.insert(new Coffee(2, "Laa",false, new Timestamp(new Date().getTime())));
        for (Coffee c : con.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
        con.update(new Coffee(2, "LOL",true, new Timestamp(new Date().getTime())));
        for (Coffee c : con.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
        con.deleteByID(2);
        for (Coffee c : con.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
    }

}