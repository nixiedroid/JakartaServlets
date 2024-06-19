package com.nixiedroid.jakarta.rest.jdbc;

import com.nixiedroid.jakarta.rest.models.Coffee;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;

public abstract class Connector {

    private static String fixL11n(String bad) {
        try {
            return new String(bad.getBytes(StandardCharsets.UTF_8), "Windows-1251");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Coffee getCoffeeById(int id) {
        try (Connection connection = connect(); PreparedStatement getById = connection.prepareStatement("SELECT * FROM site.coffee_list WHERE id = ?")) {
            getById.setInt(1, id);
            ResultSet resultSet = getById.executeQuery();
            if (resultSet.next()) {
                int res_id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Timestamp ts = resultSet.getTimestamp(3);
                return new Coffee(res_id, name, ts);
            }
        } catch (SQLException e) {
            throw new RuntimeException(fixL11n(e.getMessage()));
        }
        return null;
    }

    public int insert(Coffee coffee) {
        if (getCoffeeById(coffee.getId()) == null) {
            try (Connection connection = connect(); PreparedStatement insertInto = connection.prepareStatement("INSERT INTO site.coffee_list (id, name) values (?,?)")) {
                insertInto.setInt(1, coffee.getId());
                insertInto.setString(2, coffee.getName());
                return insertInto.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(fixL11n(e.getMessage()));
            }
        }
        return 0;
    }

    public int update(Coffee coffee) {
        if (getCoffeeById(coffee.getId()) != null) {
            try (Connection connection = connect(); PreparedStatement update = connection.prepareStatement("UPDATE site.coffee_list  SET  name=?, created=? WHERE id=?")) {
                update.setString(1, coffee.getName());
                update.setTimestamp(2, coffee.getCreated());
                update.setInt(3, coffee.getId());
                return update.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(fixL11n(e.getMessage()));
            }
        }
        return 0;
    }

    public ArrayList<Coffee> getAllCoffees() {
        ArrayList<Coffee> coffees = new ArrayList<>();
        try (Connection connection = connect(); PreparedStatement getAll = connection.prepareStatement("SELECT * FROM site.coffee_list")) {
            ResultSet resultSet = getAll.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Timestamp ts = resultSet.getTimestamp(3);
                Coffee coffee = new Coffee(id, name, ts);
                coffees.add(coffee);
            }
        } catch (SQLException e) {
            throw new RuntimeException(fixL11n(e.getMessage()));
        }
        return coffees;
    }

    public int deleteByID(int id) {
        try (Connection connection = connect(); PreparedStatement del = connection.prepareStatement("DELETE FROM site.coffee_list WHERE id = ?")) {
            del.setInt(1, id);
            return del.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(fixL11n(e.getMessage()));
        }
    }

    protected abstract Connection connect() throws SQLException;
}
