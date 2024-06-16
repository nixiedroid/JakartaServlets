package com.nixiedroid.jakarta.rest;

import com.nixiedroid.jakarta.rest.servlets.HttpServlet;
import com.nixiedroid.jakarta.rest.servlets.MethodsServlet;
import com.nixiedroid.jakarta.rest.servlets.RootServlet;
import com.nixiedroid.jakarta.rest.servlets.ServletLoader;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class AppLauncher {
    private final Tomcat tomcat = new Tomcat();

    AppLauncher() throws LifecycleException {
        initTomcat();
        tomcat.start();
        tomcat.getServer().await();
    }
    //"C:\Program Files\Java\jdk-17.0.2\bin\jar.exe"
    //jar -xvf rest-1.0-SNAPSHOT.war
    //java -cp "WEB-INF\classes\;WEB-INF\lib\*" com.nixiedroid.jakarta.rest.AppLauncher

    //"C:\Program Files\Java\jdk-17.0.2\bin\jar.exe" -xvf rest-1.0-SNAPSHOT.war && java -cp "WEB-INF\classes\;WEB-INF\lib\*" com.nixiedroid.jakarta.rest.AppLauncher
    public static void main(String[] args) {
        try {
            new AppLauncher();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initTomcat() {
        tomcat.getService().addConnector(createConnector());
        tomcat.setPort(getPort());
        initTomcatCtx();
    }

    private void initTomcatCtx() {
        tomcat.setBaseDir(".");
        String contextPath = "";
        String docBase = new File(".").getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);
        ServletLoader loader = new ServletLoader(tomcat,context);
        loader.loadServlet(MethodsServlet.class);
        loader.loadServlet(RootServlet.class);
        loader.loadServlet(HttpServlet.class);
    }

    private Connector createConnector() {
        final Connector connector = new Connector();
        connector.setPort(getPort());
        return connector;
    }

    private int getPort() {
        try {
            return Integer.parseInt(System.getenv("jakarta-port"));
        } catch (NumberFormatException ignored) {
            return 8080;
        }
    }
}
