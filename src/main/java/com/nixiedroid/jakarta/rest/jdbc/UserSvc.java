package com.nixiedroid.jakarta.rest.jdbc;

import com.nixiedroid.jakarta.rest.models.User;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserSvc {
    private final Connector connector;

    public UserSvc(Connector connector) {
        this.connector = connector;
    }

    public User getUserById(int id) {
        String SQL = "SELECT * FROM site.users WHERE id = ?";
        return connector.execute(SQL, getById -> {
            getById.setInt(1, id);
            ResultSet resultSet = getById.executeQuery();
            if (resultSet.next()) {
                int res_id = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                Timestamp ts = resultSet.getTimestamp(4);
                return new User(res_id, firstName, lastName, ts);
            }
            return null;
        });
    }

    public void insert(User User) {
        if (getUserById(User.getId()) == null) {
            String SQL = "INSERT INTO site.users  (name,has_milk) values (?,?)";
            connector.execute(SQL, f -> {
                f.setString(1, User.getFirstName());
                f.setString(2, User.getLastName());
                return f.executeUpdate();
            });
        }
    }

    public int update(User User) {
        User editUser = getUserById(User.getId());
        if (editUser != null) {
            String SQL = "UPDATE site.users   SET  name=?, created=?, has_milk=? WHERE id=?";
            return connector.execute(SQL, f -> {
                f.setString(1, User.getFirstName());
                f.setTimestamp(2, User.getCreated());
                f.setString(3, User.getLastName());
                f.setInt(4, editUser.getId());
                return f.executeUpdate();
            });
        }
        return 0;
    }

    public ArrayList<User> findByName(String firstName,String lastName) {
        ArrayList<User> Users = new ArrayList<>();
        String SQL = "SELECT * FROM site.users  WHERE firstName = ? AND lastName =? ORDER BY id";
        return connector.execute(SQL, f -> {
            f.setString(1, firstName);
            f.setString(2,lastName);
            ResultSet resultSet = f.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstName1 = resultSet.getString(2);
                String lastName1 = resultSet.getString(3);
                Timestamp ts = resultSet.getTimestamp(4);
                User User = new User(id, firstName1, lastName1, ts);
                Users.add(User);
            }
            return Users;
        });
    }


    public ArrayList<User> getAllUsers() {
        ArrayList<User> Users = new ArrayList<>();
        String SQL = "SELECT * FROM site.users  ORDER BY id";
        return connector.execute(SQL,getAll->{
            ResultSet resultSet = getAll.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                Timestamp ts = resultSet.getTimestamp(4);
                User User = new User(id, firstName, lastName, ts);
                Users.add(User);
            }
            return Users;
        });
    }

    public void deleteByID(int id) {
        String SQL1 = "DELETE FROM site.favourite_Users WHERE user_id = ?";
        String SQL2 = "DELETE FROM site.users  WHERE id = ?";
        connector.execute(SQL1, delFav -> {
            delFav.setInt(1, id);
            return delFav.executeUpdate();
        });
        connector.execute(SQL2, delUser -> {
            delUser.setInt(1, id);
            return delUser.executeUpdate();
        });
    }
}
