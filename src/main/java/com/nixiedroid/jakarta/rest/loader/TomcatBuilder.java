package com.nixiedroid.jakarta.rest.loader;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class TomcatBuilder {
    public static Tomcat build(int port) {
        Tomcat tomcat = new Tomcat();
        tomcat.getService().addConnector(createConnector(port));
        tomcat.setPort(getPort(port));
        return tomcat;
    }

    private static Connector createConnector(int port) {
        final Connector connector = new Connector();
        connector.setPort(getPort(port));
        return connector;
    }

    private static int getPort(int port) {
        try {
            return Integer.parseInt(System.getenv("jakarta-port"));
        } catch (NumberFormatException ignored) {
            return port;
        }
    }
}
