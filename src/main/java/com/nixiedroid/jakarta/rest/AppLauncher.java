package com.nixiedroid.jakarta.rest;

import com.nixiedroid.jakarta.rest.loader.ServletLoader;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AppLauncher {
    private final Tomcat tomcat = new Tomcat();

    AppLauncher() throws LifecycleException {
        System.out.println(System.getProperty("java.io.tmpdir"));
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
        String srcWebappPath = "src/main/webapp/";
        Context context;
        if (Files.exists(Path.of(srcWebappPath))) {  //Running inside IDE
            context = tomcat.addWebapp("", new File(srcWebappPath).getAbsolutePath());
        } else {
            context = tomcat.addWebapp("", new File(".").getAbsolutePath());
        }
        if (isJarFile()) {
            extractWebXml(context);
        }
        new ServletLoader(tomcat, context).findAndLoadServlets();
    }

    private void extractWebXml(Context context) {
        File dest = new File("web.xml");
        try (InputStream is = getClass().getResourceAsStream("/META-INF/resources/WEB-INF/web.xml")) {
            if (is != null) {
                Files.copy(is, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    File f = new File("web.xml");
                    if (f.exists()) f.delete();
                }));
            }
            context.setAltDDName("web.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isJarFile() {
        String fName = ServletLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return fName.endsWith(".jar");
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
