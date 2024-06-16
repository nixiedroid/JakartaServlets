package com.nixiedroid.jakarta.rest.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/root")
public class RootServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        // получаем параметр id
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<h1>Page not found</h1>");
            writer.println(req.getRequestURI());
        }
    }
}
