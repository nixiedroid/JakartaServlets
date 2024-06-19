package com.nixiedroid.jakarta.rest.jdbc;

import jdk.jfr.StackTrace;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnector extends Connector{

    @Override
    protected Connection connect() throws SQLException {
       return  DriverManager.getConnection(
               "jdbc:postgresql://localhost:5432/coffees",
               "coffees",
               "1234");
    }
}
