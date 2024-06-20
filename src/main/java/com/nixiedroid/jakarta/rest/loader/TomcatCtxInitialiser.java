package com.nixiedroid.jakarta.rest.loader;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TomcatCtxInitialiser {
    public static void init(Tomcat tomcat) {
        System.out.println(System.getProperty("java.io.tmpdir"));
        initTomcatCtx(tomcat);
    }

    private static void initTomcatCtx(Tomcat tomcat) {
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

    private static void extractWebXml(Context context) {
        File dest = new File("web.xml");
        try (InputStream is = TomcatCtxInitialiser.class.getResourceAsStream("/META-INF/resources/WEB-INF/web.xml")) {
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

    private static boolean isJarFile() {
        String fName = TomcatCtxInitialiser.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return fName.endsWith(".jar");
    }


}
