package com.nixiedroid.jakarta.rest;

import com.nixiedroid.jakarta.rest.servlets.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class WebXmlServlet  extends jakarta.servlet.http.HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<h1>Web XML servlet OK</h1>");
            writer.println("<h2>"+req.getRequestURI()+"</h2>");
        }
    }
}
