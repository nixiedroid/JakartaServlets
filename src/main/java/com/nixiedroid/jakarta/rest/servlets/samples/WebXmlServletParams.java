package com.nixiedroid.jakarta.rest.servlets.samples;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class WebXmlServletParams extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        String appName = getServletContext().getInitParameter("appName");
        String servletVer = getServletConfig().getInitParameter("servletVersion");
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("<h1>Web XML servlet OK</h1>");
            writer.println("<h1>App Name is "+ appName  +"</h1>");
            writer.println("<h1>Servlet Version is "+ servletVer  +"</h1>");
            writer.println("<h2>"+req.getRequestURI()+"</h2>");
        }
    }
}
