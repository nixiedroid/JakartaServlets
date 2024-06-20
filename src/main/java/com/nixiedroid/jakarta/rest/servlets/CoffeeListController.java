package com.nixiedroid.jakarta.rest.servlets;

import com.nixiedroid.jakarta.rest.jdbc.Connector;
import com.nixiedroid.jakarta.rest.jdbc.PostgresConnector;
import com.nixiedroid.jakarta.rest.models.Coffee;
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
        List<Coffee> coffees = con.getAllCoffees();
        req.setAttribute("coffees", coffees);
        getServletContext().getRequestDispatcher("/coffees.jsp").forward(req, res);
    }

}
