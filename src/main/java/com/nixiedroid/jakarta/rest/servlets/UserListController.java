package com.nixiedroid.jakarta.rest.servlets;

import com.nixiedroid.jakarta.rest.jdbc.PostgresConnector;
import com.nixiedroid.jakarta.rest.jdbc.UserSvc;
import com.nixiedroid.jakarta.rest.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/users")
public class UserListController extends HttpServlet {
    UserSvc con = new UserSvc(new PostgresConnector());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        list(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String action = req.getServletPath();
        switch (action) {
            case "/delete":
                delete(req, res);
                break;
            case "/edit":
                edit(req, res);
                break;
            case "/editSubmit":
                editSubmit(req, res);
                break;
            case "/findUser":
                find(req, res);
                break;
            case "/add":
                add(req, res);
                break;
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<User> Users = con.getAllUsers();
        req.setAttribute("Users", Users);
        getServletContext().getRequestDispatcher("/Users.jsp").forward(req, res);
    }


    private void delete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String id = req.getParameter("id");
        if (id != null) con.deleteByID(Integer.parseInt(id));
        res.sendRedirect(req.getContextPath() + "/");
    }

    private void edit(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String idString = req.getParameter("id");
        int id = (idString == null || idString.isEmpty()) ? -1 : Integer.parseInt(idString);
        req.setAttribute("User", con.getUserById(id));
        getServletContext().getRequestDispatcher("/editUser.jsp").forward(req, res);
    }

    private void editSubmit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String idString = req.getParameter("id");
        int id = (idString == null || idString.isEmpty()) ? -1 : Integer.parseInt(idString);
        if (id > 0) {
            User edited = con.getUserById(id);
            if (edited != null) {
                String firstName = req.getParameter("first_name");
                String lastName = req.getParameter("last_name");
                if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
                    edited.setFirstName(firstName);
                    edited.setLastName(lastName);
                }
                con.update(edited);
            }
        }
        res.sendRedirect("/");
    }

    private void find(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<User> Users;
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            Users = con.findByName(firstName, lastName);
        } else Users = new ArrayList<>();
        req.setAttribute("Users", Users);
        getServletContext().getRequestDispatcher("/findUser.jsp").forward(req, res);
    }

    private void add(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        if (firstName != null && !firstName.isEmpty() && lastName != null && !lastName.isEmpty()) {
            con.insert(new User(firstName, lastName));
            res.sendRedirect(req.getContextPath() + "/");
        }
    }
}
