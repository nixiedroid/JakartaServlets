package com.nixiedroid.jakarta.rest.loader;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Tomcat's classloader is not applied to embedded Tomcat <br>
 * <a href="https://stackoverflow.com/a/78611382">StackOverflow</a>
 * <p>
 * {@link ServletLoader} is designed to obtain mappings from
 * {@link jakarta.servlet.annotation.WebServlet} annotation within Servlet classes
 * and improve readability of code
 */
public final class ServletLoader {

    private final Tomcat tomcat;
    private final Context ctx;
    private final static String SERVLETS_PKG = "servlets";

    /**
     * Constructor for Servlet loader class
     *
     * @param cat - {@link org.apache.catalina.startup.Tomcat} instance
     */
    public ServletLoader(final Tomcat cat, Context ctx) {
        this.tomcat = cat;
        this.ctx = ctx;
        List<String> classes = getServletClassPaths();
        if (classes != null) {
            loadServletClasses(classes);
        }
    }

    /**
     * Removes file extension from fileName:<br>
     * file.txt -> file
     *
     * @param fileName
     * @return file name without extension
     */

    private String removeFileExtension(String fileName) {
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return fileName;
        }
    }

    private String getServletsPackageName() {
        String pkgName = this.getClass().getPackageName();
        if (pkgName.isEmpty()) return SERVLETS_PKG;
        int i = pkgName.lastIndexOf('.');
        if (i == -1) {
            i = pkgName.length();
        }
        return pkgName.substring(0, i) + "." + SERVLETS_PKG;
    }

    private void loadServletClasses(List<String> classPaths) {
        for (String clz : classPaths) {
            try {
                Class<?> clazz = Class.forName(clz);
                loadServlet(clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<String> getServletClassPaths() {
        String classPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (classPath.endsWith(".jar")) {
            return loadServletsFromJar(classPath.replace("%20", " "));
        } else {
            return loadExplodedServlets(classPath);
        }
    }

    public List<String> loadExplodedServlets(String classPath) {
        File classes = new File(classPath + getServletsPackageName().replace('.', '/'));
        if (classes.exists()) {
            File[] fClasses = classes.listFiles();
            if (fClasses == null) return null;
            List<String> classNames = new ArrayList<>();
            for (File f : fClasses) {
                classNames.add(getServletsPackageName() + "." + removeFileExtension(f.getName()));
            }
            return classNames;
        }
        return null;
    }

    public List<String> loadServletsFromJar(String jarPath) {
        try {
            List<String> classNames = new ArrayList<>();
            ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    if (className.startsWith(getServletsPackageName())) {
                        classNames.add(className.substring(0, className.length() - ".class".length()));
                    }
                }
            }
            return classNames;
        } catch (IOException e) {
            return null;
        }
    }

    public void loadServlet(Class<?> servletClass) {
        loadServlet(servletClass, new String[]{});
    }

    @SuppressWarnings("unused")
    public void loadServlet(Class<?> servletClass, String url) {
        loadServlet(servletClass, new String[]{url});
    }

    private void placeMappings(WebServlet annotation, final String[] urlPatterns, final String servletName) {
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
        if (servletClass.getSuperclass() != null &&
                servletClass.getSuperclass().equals(HttpServlet.class)) {
            try {
                String servletName = servletClass.getSimpleName();
                WebServlet servletAnnotation = servletClass.getAnnotation(WebServlet.class);
                HttpServlet servlet = (HttpServlet) servletClass.getConstructor().newInstance();
                tomcat.addServlet(ctx.getPath(), servletName, servlet);
                placeMappings(servletAnnotation, urlPatterns, servletName);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else throw new IllegalArgumentException(servletClass.getName() + " is not valid servlet class");
    }
}
