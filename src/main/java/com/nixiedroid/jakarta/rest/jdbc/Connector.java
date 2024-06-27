package com.nixiedroid.jakarta.rest.jdbc;

import com.nixiedroid.jakarta.rest.models.Coffee;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;

public abstract class Connector {

    public Connector() {
        try {
            Class.forName(getDriverName()).getConstructor().newInstance();
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


    private Connection connect() throws SQLException {
        String url = "jdbc:" + getUrlBase() + "://" + getHost() + ":" + getPort() + "/" + getDBName();
        return DriverManager.getConnection(url, getUserName(), getPassword());
    }

    public Coffee getCoffeeById(int id) {
        try (Connection connection = connect(); PreparedStatement getById = connection.prepareStatement("SELECT * FROM site.coffees WHERE id = ?")) {
            getById.setInt(1, id);
            ResultSet resultSet = getById.executeQuery();
            if (resultSet.next()) {
                int res_id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                boolean has_milk = resultSet.getBoolean(3);
                Timestamp ts = resultSet.getTimestamp(4);
                return new Coffee(res_id, name, has_milk, ts);
            }
        } catch (SQLException e) {
            throw new RuntimeException(fixL11n(e.getMessage()));
        }
        return null;
    }

    public int insert(Coffee coffee) {
        if (getCoffeeById(coffee.getId()) == null) {
            try (Connection connection = connect(); PreparedStatement insertInto = connection.prepareStatement("INSERT INTO site.coffees (name,has_milk) values (?,?)")) {
                insertInto.setString(1, coffee.getName());
                insertInto.setBoolean(2,coffee.isHas_milk());;
                return insertInto.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(fixL11n(e.getMessage()));
            }
        }
        return 0;
    }

    public int update(Coffee coffee) {
        Coffee editCoffee = getCoffeeById(coffee.getId()) ;
        if (editCoffee != null) {
            try (Connection connection = connect(); PreparedStatement update = connection.prepareStatement("UPDATE site.coffees  SET  name=?, created=?, has_milk=? WHERE id=?")) {
                update.setString(1, coffee.getName());
                update.setTimestamp(2, coffee.getCreated());
                update.setBoolean(3,coffee.isHas_milk());;
                update.setInt(4, editCoffee.getId());
                return update.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(fixL11n(e.getMessage()));
            }
        }
        return 0;
    }

    public ArrayList<Coffee> findByName(String name){
        ArrayList<Coffee> coffees = new ArrayList<>();
        try (Connection connection = connect(); PreparedStatement getAll = connection.prepareStatement("SELECT * FROM site.coffees WHERE name = ? ORDER BY id")) {
            getAll.setString(1,name);
            ResultSet resultSet = getAll.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String Cname = resultSet.getString(2);
                boolean has_milk = resultSet.getBoolean(3);
                Timestamp ts = resultSet.getTimestamp(4);
                Coffee coffee = new Coffee(id, Cname, has_milk, ts);
                coffees.add(coffee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(fixL11n(e.getMessage()));
        }
        return coffees;
    }

    public ArrayList<Coffee> getAllCoffees() {
        ArrayList<Coffee> coffees = new ArrayList<>();
        try (Connection connection = connect(); PreparedStatement getAll = connection.prepareStatement("SELECT * FROM site.coffees ORDER BY id")) {
            ResultSet resultSet = getAll.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                boolean has_milk = resultSet.getBoolean(3);
                Timestamp ts = resultSet.getTimestamp(4);
                Coffee coffee = new Coffee(id, name, has_milk, ts);
                coffees.add(coffee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(fixL11n(e.getMessage()));
        }
        return coffees;
    }

    public int deleteByID(int id) {
        try (Connection connection = connect();
             PreparedStatement delCoffee = connection.prepareStatement("DELETE FROM site.coffees WHERE id = ?");
        PreparedStatement delFav = connection.prepareStatement("DELETE FROM site.favourite_coffees WHERE coffee_id = ?")
        ) {
            delFav.setInt(1, id);
            delFav.executeUpdate();
            delCoffee.setInt(1,id);
            return delCoffee.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(fixL11n(e.getMessage()));
        }
    }


    protected abstract String getDriverName();

    protected abstract String getUrlBase();

    protected abstract String getHost();

    protected abstract String getPort();

    protected abstract String getDBName();

    protected abstract String getUserName();

    protected abstract String getPassword();
}
