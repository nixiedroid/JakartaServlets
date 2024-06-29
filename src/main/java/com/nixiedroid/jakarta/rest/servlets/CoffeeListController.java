package com.nixiedroid.jakarta.rest.servlets;

import com.nixiedroid.jakarta.rest.jdbc.CoffeeSvc;
import com.nixiedroid.jakarta.rest.jdbc.PostgresConnector;
import com.nixiedroid.jakarta.rest.models.Coffee;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoffeeListController extends HttpServlet {
    CoffeeSvc svc = new CoffeeSvc(new PostgresConnector());

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
            case "/findCoffee":
                find(req, res);
                break;
            case "/add":
                add(req, res);
                break;
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Coffee> coffees = svc.getAllCoffees();
        req.setAttribute("coffees", coffees);
        getServletContext().getRequestDispatcher("/coffees.jsp").forward(req, res);
    }
    private void delete(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String id = req.getParameter("id");
        if (id!= null) svc.deleteByID(Integer.parseInt(id));
        res.sendRedirect(req.getContextPath() + "/");
    }
    private void edit(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String idString = req.getParameter("id");
        int id = (idString==null || idString.isEmpty())? -1 : Integer.parseInt(idString);
        req.setAttribute("coffee", svc.getCoffeeById(id));
        getServletContext().getRequestDispatcher("/editCoffee.jsp").forward(req, res);
    }
    private void editSubmit(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String idString = req.getParameter("id");
        int id = (idString==null || idString.isEmpty())? -1 : Integer.parseInt(idString);
        if (id >0) {
            Coffee edited = svc.getCoffeeById(id);
            if (edited!= null) {
                String name = req.getParameter("name");
                String hasMilk = req.getParameter("hasMilk");
                boolean hasmilk = (hasMilk != null && hasMilk.equals("on"));
                if (name != null && !name.isEmpty()) {
                    edited.setName(name);
                }
                edited.setHas_milk(hasmilk);
                svc.update(edited);
            }
        }
        res.sendRedirect("/");
    }
    private void find(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Coffee> coffees;
        String name = req.getParameter("name");
        if (name != null && !name.isEmpty()) {
            coffees = svc.findByName(name);
        } else coffees = new ArrayList<>();
        req.setAttribute("coffees", coffees);
        getServletContext().getRequestDispatcher("/findCoffee.jsp").forward(req, res);
    }
    private void add(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = req.getParameter("name");
        String hasMilk = req.getParameter("hasMilk");
        boolean hasmilk = (hasMilk!=null && hasMilk.equals("on"));
        if (name!= null && !name.isEmpty()) svc.insert(new Coffee(name,hasmilk));
        res.sendRedirect(req.getContextPath() + "/");
    }

}
