package com.nixiedroid.jakarta.rest;

import com.nixiedroid.jakarta.rest.loader.ServletLoader;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

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

    //"C:\Program Files\Java\jdk-17.0.2\bin\jar.exe" -xvf JakartaServlets-1.0-SNAPSHOT.war && java -cp "WEB-INF\classes\;WEB-INF\lib\*" com.nixiedroid.jakarta.rest.AppLauncher
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
        String webappExplodedLocation = "src/main/webapp/";
        String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        Context context;
        if (classPath.endsWith(".jar")) {
            String basedir;
            try {
                Field f = tomcat.getClass().getDeclaredField("basedir");
                f.setAccessible(true);
                basedir = (String) f.get(tomcat);
                f.setAccessible(false);
            } catch (ReflectiveOperationException e) {
                basedir = System.getProperty("java.io.tmpdir");
            }
            context = tomcat.addWebapp("/", basedir);
        } else if (Files.exists(Path.of(webappExplodedLocation))) {
            context = tomcat.addWebapp("/", new File(webappExplodedLocation).getAbsolutePath());
        } else {
            context = tomcat.addWebapp("/", new File(".").getAbsolutePath());
        }
        new ServletLoader(tomcat, context);
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
