package com.nixiedroid.jakarta.rest.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/http")
public class HttpServlet extends jakarta.servlet.http.HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        try (PrintWriter writer = resp.getWriter()) {
            String id = req.getParameter("id");
            writer.println("<h2>Id:" + id + "</h2>");
        }
    }
}
