package com.nixiedroid.jakarta.rest.jdbc;

import com.nixiedroid.jakarta.rest.models.Coffee;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

class ConnectorTest {
    @Test
    void test() {
        CoffeeSvc svc = new CoffeeSvc(new PostgresConnector());
        for (Coffee c : svc.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
        svc.insert(new Coffee(2, "Laa",false, new Timestamp(new Date().getTime())));
        for (Coffee c : svc.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
        svc.update(new Coffee(2, "LOL",true, new Timestamp(new Date().getTime())));
        for (Coffee c : svc.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
        svc.deleteByID(2);
        for (Coffee c : svc.getAllCoffees()) {
            System.out.println(c.getId() + " - " + c.getName());
        }
    }

}