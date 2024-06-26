package com.nixiedroid.jakarta.rest.servlets;

import com.nixiedroid.jakarta.rest.jdbc.Connector;
import com.nixiedroid.jakarta.rest.jdbc.PostgresConnector;
import com.nixiedroid.jakarta.rest.models.Coffee;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class CoffeeListController extends HttpServlet {
    Connector con = new PostgresConnector();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String action = req.getServletPath();
        if (action.equals("/add")) {
            add(req, res);
        } else {
            list(req, res);
        }
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
            case "/add":
                add(req, res);
                break;
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Coffee> coffees = con.getAllCoffees();
        req.setAttribute("coffees", coffees);
        getServletContext().getRequestDispatcher("/coffees.jsp").forward(req, res);
    }
    private void delete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id!= null) con.deleteByID(Integer.parseInt(id));
        res.sendRedirect(req.getContextPath() + "/");
    }
    private void edit(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String idString = req.getParameter("id");
        int id = (idString==null || idString.isEmpty())? -1 : Integer.parseInt(idString);
        req.setAttribute("coffee", con.getCoffeeById(id));
        RequestDispatcher rd = req.getRequestDispatcher("/");
        rd.forward(req, res);
        //res.sendRedirect(req.getContextPath() + "/");
    }
    private void find(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Coffee> coffees = con.getAllCoffees();
        req.setAttribute("coffees", coffees);
        getServletContext().getRequestDispatcher("/coffees.jsp").forward(req, res);
    }
    private void add(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String name = req.getParameter("name");
        String hasMilk = req.getParameter("hasMilk");
        boolean hasmilk = (hasMilk!=null && hasMilk.equals("on"));
        if (name!= null && !name.isEmpty()) con.insert(new Coffee(name,hasmilk));
        res.sendRedirect(req.getContextPath() + "/");
    }

}
