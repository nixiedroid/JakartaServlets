package com.nixiedroid.jakarta.rest;

import com.nixiedroid.jakarta.rest.loader.TomcatEmbedStarter;
import org.apache.catalina.LifecycleException;

public class AppLauncher {

    //"C:\Program Files\Java\jdk-17.0.2\bin\jar.exe"
    //jar -xvf rest-1.0-SNAPSHOT.war
    //java -cp "WEB-INF\classes\;WEB-INF\lib\*" com.nixiedroid.jakarta.rest.AppLauncher

    //"C:\Program Files\Java\jdk-17.0.2\bin\jar.exe" -xvf JakartaServlets-1.0-SNAPSHOT.war && java -cp "WEB-INF\classes\;WEB-INF\lib\*" com.nixiedroid.jakarta.rest.AppLauncher
    public static void main(String[] args) {
        try {
            TomcatEmbedStarter.start(8087);
        } catch (LifecycleException e){
            throw new RuntimeException(e);
        }
    }
}
