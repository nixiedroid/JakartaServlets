package com.nixiedroid.jakarta.rest.servlets.samples;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServlet;

import java.io.IOException;
import java.io.PrintWriter;

public class WebXmlServlet  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<h1>Web XML servlet OK</h1>");
            writer.println("<h2>"+req.getRequestURI()+"</h2>");
        }
    }
}
