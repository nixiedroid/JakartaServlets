package com.nixiedroid.jakarta.rest.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

/**
 * Tomcat's classloader is not applied to embedded Tomcat <br>
 * <a href="https://stackoverflow.com/a/78611382">StackOverflow</a>
 * <p>
 * {@link com.nixiedroid.jakarta.rest.servlets.ServletLoader} is designed to obtain mappings from
 * {@link jakarta.servlet.annotation.WebServlet} annotation within Servlet classes
 * and improve readability of code
 */
public final class ServletLoader {

    private final static Class<HttpServlet> httpServletClass = HttpServlet.class;
    private final Tomcat tomcat;
    private final Context ctx;

    /**
     * Constructor for Servlet loader class
     *
     * @param cat - {@link org.apache.catalina.startup.Tomcat} instance
     */
    public ServletLoader(final Tomcat cat, Context ctx) {
        System.out.println(ServletLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        this.tomcat = cat;
        this.ctx = ctx;
    }

    public void loadServlet(Class<?> servletClass) {
        loadServlet(servletClass, new String[]{});
    }

    public void loadServlet(Class<?> servletClass, String url) {
        loadServlet(servletClass, new String[]{url});
    }

    private void placeMappings(WebServlet annotation, final String[] urlPatterns,final String servletName){
        if (urlPatterns == null && annotation == null) return;
        if (urlPatterns != null && urlPatterns.length > 0) {
            for (String url : urlPatterns) {
                ctx.addServletMappingDecoded(url, servletName);
            }
        } else if (annotation != null) {
            if (annotation.value().length > 0) {
                for (String url : annotation.value()) {
                    ctx.addServletMappingDecoded(url, servletName);
                }
            } else {
                for (String url : annotation.urlPatterns()) {
                    ctx.addServletMappingDecoded(url, servletName);
                }
            }
        }
    }

    public void loadServlet(Class<?> servletClass, final String[] urlPatterns) {
        if (servletClass == null) throw new IllegalArgumentException("Servlet class must not be null");
        if (servletClass.getSuperclass().equals(httpServletClass)) {
            try {
                String servletName = servletClass.getSimpleName();
                WebServlet servletAnnotation = servletClass.getAnnotation(WebServlet.class);
                HttpServlet servlet = (HttpServlet) servletClass.getConstructor().newInstance();
                tomcat.addServlet(ctx.getPath(), servletName, servlet);
                placeMappings(servletAnnotation,urlPatterns,servletName);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else throw new IllegalArgumentException(servletClass.getName() + " is not valid servlet class");
    }
}
