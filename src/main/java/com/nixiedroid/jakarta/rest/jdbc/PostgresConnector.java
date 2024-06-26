package com.nixiedroid.jakarta.rest.jdbc;

public class PostgresConnector extends Connector {
    @Override
    protected String getDriverName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected String getUserName() {
        return "coffees";
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
        return "5432";
    }

    @Override
    protected String getUrlBase() {
        return "postgresql";
    }
}
