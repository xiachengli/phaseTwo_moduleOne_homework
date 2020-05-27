package container;

import classLoad.MyClassLoader;
import server.HttpServlet;

import static java.rmi.server.RMIClassLoader.loadClass;

public class StandardWrapper {

    //appBase+context
    private String path;
    //servlet-class
    private String servletClass;
    //HttpServlet
    public HttpServlet servlet;

    public StandardWrapper(String path, String servletClass) throws IllegalAccessException, InstantiationException {
        this.path = path;
        this.servletClass = servletClass;
        loadMyClass(path,servletClass);
    }

    private void loadMyClass(String path, String servletClass) throws IllegalAccessException, InstantiationException {
        MyClassLoader myClassLoader = new MyClassLoader(path);
        Class cls = myClassLoader.getClassByName(servletClass);
        servlet = (HttpServlet)cls.newInstance();
    }
}
