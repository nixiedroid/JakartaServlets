package com.nixiedroid.jakarta.rest.loader;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final String httpServletPath = HttpServlet.class.getName().replace('.', '/');
    private final Tomcat tomcat;
    private final Context ctx;

    /**
     * Constructor for Servlet loader class
     *
     * @param cat - {@link org.apache.catalina.startup.Tomcat} instance
     */
    public ServletLoader(final Tomcat cat, Context ctx) {
        this.tomcat = cat;
        this.ctx = ctx;
    }

    private static String getRootPackageName() {
        String pkgName = ServletLoader.class.getPackageName();
        if (pkgName.isEmpty()) return "";
        int i = pkgName.lastIndexOf('.');
        if (i == -1) {
            i = pkgName.length();
        }
        return pkgName.substring(0, i);
    }

    /**
     * Finds all class files, extending {@link jakarta.servlet.http.HttpServlet}
     * in exploded file tree or inside JAR file
     *
     * @return list of relative ClassPaths
     */
    private static List<String> findServletClasses() {
        String codeSrc = ServletLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (isJarFile()) {
            return findServletClassesJar(codeSrc.replace("%20", " "));
        } else {
            return findServletClassesFileTree(codeSrc);
        }
    }

    /**
     * Checks if Application is Running from jar file
     *
     * @return boolean value
     */
    private static boolean isJarFile() {
        return ServletLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar");
    }


    private static List<String> findServletClassesFileTree(String classPath) {
        File classes = new File(classPath + getRootPackageName().replace('.', '/'));
        if (classes.exists()) {
            List<String> classNames = new ArrayList<>();
            parseFileTree(classes, classNames);
            return classNames;
        }
        return null;
    }

    private static void parseFileTree(File root, List<String> classNames) {
        File[] fClasses = root.listFiles();
        if (fClasses == null) return;
        for (File f : fClasses) {
            if (f.isDirectory()) {
                parseFileTree(f, classNames);
            } else {
                try {
                    checkAndAdd(Files.readAllBytes(Path.of(f.toURI())), classNames);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private static byte[] read(ZipInputStream zis, int size) throws IOException {
        byte[] bytes = new byte[size];
        int read = 0;
        while (read < size) {
            read += zis.read(bytes, read, (size - read));
        }
        return bytes;
    }

    private static void checkAndAdd(byte[] classFile, List<String> classNames) {
        ClassParser.RawInfo info = ClassParser.retrieveInfo(classFile);
        if (info != null && info.superClassName.equals(httpServletPath)) {
            classNames.add(info.name);
        }
    }

    private static List<String> findServletClassesJar(String jarPath) {
        try {
            List<String> classNames = new ArrayList<>();
            ZipInputStream zip = new ZipInputStream(new FileInputStream(jarPath));
            for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    if (className.startsWith(getRootPackageName())) {
                        checkAndAdd(read(zip, (int) entry.getSize()), classNames);
                    }
                }
            }
            return classNames;
        } catch (IOException e) {
            return null;
        }
    }

    public void findAndLoadServlets() {
        List<String> classes = findServletClasses();
        loadServletClasses(classes);
    }

    private void loadServletClasses(List<String> fullClassNames) {
        if (fullClassNames == null) return;
        for (String className : fullClassNames) {
            try {
                Class<?> clazz = Class.forName(className.replace('/', '.'));
                loadServlet(clazz);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void placeMappings(WebServlet annotation, final String servletName) {
        if (annotation == null) return;
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

    private void loadServlet(Class<?> servletClass) {
        if (servletClass == null) throw new IllegalArgumentException("Servlet class must not be null");
        if (servletClass.getSuperclass() != null && servletClass.getSuperclass().equals(HttpServlet.class)) {
            try {
                String servletName = servletClass.getSimpleName();
                WebServlet servletAnnotation = servletClass.getAnnotation(WebServlet.class);
                if (servletAnnotation != null) {
                    HttpServlet servlet = (HttpServlet) servletClass.getConstructor().newInstance();
                    tomcat.addServlet(ctx.getPath(), servletName, servlet);
                    placeMappings(servletAnnotation, servletName);
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else throw new IllegalArgumentException(servletClass.getName() + " is not valid servlet class");
    }
}
