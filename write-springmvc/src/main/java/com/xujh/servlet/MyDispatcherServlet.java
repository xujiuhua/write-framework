package com.xujh.servlet;

import com.xujh.annotation.MyController;
import com.xujh.annotation.MyRequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * <p></p>
 *
 * @author jiuhua.xu
 * @version 1.0
 * @since JDK 1.8
 */
public class MyDispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Object> ioc = new HashMap<>();

    private Map<String, Method> handlerMapping = new HashMap<>();

    private Map<String, Object> controllerMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {

        System.out.println("==MyDispatcherServlet Init...==");

        // 1. 加载配置文件
        this.doLoadConfig(config.getInitParameter("contextConfigLocation"));

        // 2. 初始化所有相关的类，扫描包
        doScanner(properties.getProperty("scanPackage"));

        // 3. 拿到扫描的类，通过反射机制，实例化，并放在ioc容器中
        doInstance();

        // 4. 初始化HandlerMapping,将url和method对应上
        initHandlerMapping();
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }

        ioc.forEach((k, v) -> {
            Class<?> clazz = v.getClass();

            // 拼url,是controller头的url拼上方法的url
            String baseUrl = "";
            if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                baseUrl = annotation.value();
            }
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(MyRequestMapping.class)) {
                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);
                    String url = annotation.value();
                    url = (baseUrl + "/" + url).replaceAll("/+", "/");

                    handlerMapping.put(url, method);
                    try {
                        controllerMap.put(url, clazz.newInstance());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    System.out.println(String.format("%s, %s", url, method));

                }
            }
        });

    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }

        classNames.forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyController.class)) {
                    ioc.put(toLowerFirstWord(clazz.getSimpleName()), clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        Arrays.stream(dir.listFiles()).forEach(file -> {
            if (file.isDirectory()) {
                //递归读取包
                doScanner(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().replace(".class", "");
                classNames.add(className);
            }
        });
    }

    private void doLoadConfig(String location) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关流
            if (null != resourceAsStream) {
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //处理请求
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500!! Server Exception");
        }

    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (handlerMapping.isEmpty()) {
            return;
        }

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        String url = uri.replace(contextPath, "").replaceAll("/+", "/");
        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 NOT FOUND");
        }

        Method method = this.handlerMapping.get(url);

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Arrays.stream(parameterAnnotations).forEach(a -> {
            System.out.println(a.length);
            Arrays.stream(a).forEach(b -> System.out.println(b.annotationType().getSimpleName()));
        });


        // 获取方法的参数列表
        Class<?>[] parameterTypes = method.getParameterTypes();

        // 获取请求的参数
        Map<String, String[]> parameterMap = req.getParameterMap();

        //保存参数值
        Object[] paramValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            String requestParam = parameterTypes[i].getSimpleName();
            if (requestParam.equals("HttpServletRequest")) {
                //参数类型已明确，这边强转类型
                paramValues[i] = req;
                continue;
            }
            if (requestParam.equals("HttpServletResponse")) {
                paramValues[i] = resp;
                continue;
            }
            if (requestParam.equals("String")) {
                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                    paramValues[i] = value;
                }
            }
        }
        // 利用反射调用方法
        try {
            method.invoke(this.controllerMap.get(url), paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把字符串的首字母小写
     *
     * @param name
     * @return
     */
    private String toLowerFirstWord(String name) {
        char[] charArray = name.toCharArray();
        charArray[0] += 32;
        return String.valueOf(charArray);
    }

}
