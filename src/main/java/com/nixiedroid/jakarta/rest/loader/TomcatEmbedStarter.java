package com.nixiedroid.jakarta.rest.loader;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class TomcatEmbedStarter {
    public static void start(int port) throws LifecycleException {
        Tomcat tomcat = TomcatBuilder.build(port);
        TomcatCtxInitialiser.init(tomcat);
        tomcat.start();
        tomcat.getServer().await();
    }
}
