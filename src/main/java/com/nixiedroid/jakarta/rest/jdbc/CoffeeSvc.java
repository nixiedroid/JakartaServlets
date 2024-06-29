package com.nixiedroid.jakarta.rest.jdbc;

import com.nixiedroid.jakarta.rest.models.Coffee;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;


public class CoffeeSvc {

    private final Connector connector;

    public CoffeeSvc(Connector connector) {
        this.connector = connector;
    }

    public Coffee getCoffeeById(int id) {
        String SQL = "SELECT * FROM site.coffees WHERE id = ?";
        return connector.execute(SQL, getById -> {
            getById.setInt(1, id);
            ResultSet resultSet = getById.executeQuery();
            if (resultSet.next()) {
                int res_id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                boolean has_milk = resultSet.getBoolean(3);
                Timestamp ts = resultSet.getTimestamp(4);
                return new Coffee(res_id, name, has_milk, ts);
            }
            return null;
        });
    }

    public void insert(Coffee coffee) {
        if (getCoffeeById(coffee.getId()) == null) {
            String SQL = "INSERT INTO site.coffees (name,has_milk) values (?,?)";
            connector.execute(SQL, insertInto -> {
                insertInto.setString(1, coffee.getName());
                insertInto.setBoolean(2, coffee.isHas_milk());
                return insertInto.executeUpdate();
            });
        }
    }

    public int update(Coffee coffee) {
        Coffee editCoffee = getCoffeeById(coffee.getId());
        if (editCoffee != null) {
            String SQL = "UPDATE site.coffees  SET  name=?, created=?, has_milk=? WHERE id=?";
            return connector.execute(SQL, f -> {
                f.setString(1, coffee.getName());
                f.setTimestamp(2, coffee.getCreated());
                f.setBoolean(3, coffee.isHas_milk());
                f.setInt(4, editCoffee.getId());
                return f.executeUpdate();
            });
        }
        return 0;
    }

    public ArrayList<Coffee> findByName(String name) {
        ArrayList<Coffee> coffees = new ArrayList<>();
        String SQL = "SELECT * FROM site.coffees WHERE name = ? ORDER BY id";
        return connector.execute(SQL, getAll -> {
            getAll.setString(1, name);
            ResultSet resultSet = getAll.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String Cname = resultSet.getString(2);
                boolean has_milk = resultSet.getBoolean(3);
                Timestamp ts = resultSet.getTimestamp(4);
                Coffee coffee = new Coffee(id, Cname, has_milk, ts);
                coffees.add(coffee);
            }
            return coffees;
        });
    }


    public ArrayList<Coffee> getAllCoffees() {
        ArrayList<Coffee> coffees = new ArrayList<>();
        String SQL = "SELECT * FROM site.coffees ORDER BY id";
        return connector.execute(SQL,getAll->{
            ResultSet resultSet = getAll.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                boolean has_milk = resultSet.getBoolean(3);
                Timestamp ts = resultSet.getTimestamp(4);
                Coffee coffee = new Coffee(id, name, has_milk, ts);
                coffees.add(coffee);
            }
            return coffees;
        });
    }

    public void deleteByID(int id) {
        String SQL = "DELETE FROM site.coffees WHERE id = ?";
        String SQL2 = "DELETE FROM site.favourite_coffees WHERE coffee_id = ?";
        connector.execute(SQL2, delFav -> {
            delFav.setInt(1, id);
            return delFav.executeUpdate();
        });
        connector.execute(SQL, delCoffee -> {
            delCoffee.setInt(1, id);
            return delCoffee.executeUpdate();
        });
    }
}
