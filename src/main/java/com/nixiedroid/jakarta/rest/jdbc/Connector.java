package com.nixiedroid.jakarta.rest.jdbc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Connector {

    public Connector() {
        try {
            ClassLoader.getSystemClassLoader().loadClass(getDriverName()).getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static String fixL11n(String bad) {
        try {
            return new String(bad.getBytes(StandardCharsets.UTF_8), "Windows-1251");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("SqlSourceToSinkFlow")
    public <R> R execute(final String SQL, ThrowableFunction<PreparedStatement, R> func) {
        try (Connection con = connect(); PreparedStatement st = con.prepareStatement(SQL)) {
            return func.apply(st);
        } catch (Throwable e) {
            if (e instanceof SQLException){
                throw new RuntimeException(fixL11n(e.getMessage()));
            }
            throw new RuntimeException(e);
        }
    }

    public Connection connect() throws SQLException {
        String url = "jdbc:" + getUrlBase() + "://" + getHost() + ":" + getPort() + "/" + getDBName();
        return DriverManager.getConnection(url, getUserName(), getPassword());
    }


    protected abstract String getDriverName();

    protected abstract String getUrlBase();

    protected abstract String getHost();

    protected abstract String getPort();

    protected abstract String getDBName();

    protected abstract String getUserName();

    protected abstract String getPassword();
}
