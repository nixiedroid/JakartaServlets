package com.nixiedroid.jakarta.rest.jdbc;

public class MySQLConnector extends Connector{
    @Override
    protected String getDriverName() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    protected String getUserName() {
        return "user";
    }

    @Override
    protected String getPassword() {
        return "1234";
    }

    @Override
    protected String getDBName() {
        return "coffees";
    }

    @Override
    protected String getHost() {
        return "localhost";
    }

    @Override
    protected String getPort() {
        return "3306";
    }

    @Override
    protected String getUrlBase() {
        return "mysql";
    }
}
