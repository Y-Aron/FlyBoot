package org.aron.boot.core;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aron.boot.annotation.web.bind.*;
import org.aron.boot.annotation.web.request.PathVariable;
import org.aron.boot.annotation.web.servlet.ControllerAdvice;
import org.aron.boot.annotation.web.servlet.ExceptionHandler;
import org.aron.boot.core.holder.HandlerHolder;
import org.aron.boot.core.impl.ApplicationRunnerChain;
import org.aron.boot.error.MappingException;
import org.aron.boot.error.holder.ErrorHolder;
import org.aron.commons.utils.TypeUtils;
import org.aron.commons.utils.Utils;
import org.aron.context.annotation.component.Component;
import org.aron.context.annotation.component.Controller;
import org.aron.context.core.ApplicationContext;
import org.aron.context.core.impl.AnnotationApplicationContext;
import org.aron.context.error.AnnotationException;
import org.aron.context.error.BeanInstantiationException;
import org.aron.server.error.ServerException;
import org.aron.server.servlet.http.HttpServletRequest;
import org.aron.server.servlet.http.HttpServletResponse;
import org.aron.server.servlet.http.enumeration.HttpRequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Y-Aron
 * @create: 2019-02-10 20:29
 */
@Slf4j
public class FlyBootContext {

    /**
     * 一个url对应一个handler
     */
    @Getter
    private Map<String, HandlerHolder> handlerMap = new ConcurrentHashMap<>(1);

    @Getter
    private Map<Class<?>, ErrorHolder> errorMap = new ConcurrentHashMap<>(1);

    @Setter
    @Getter
    private AnnotationApplicationContext annotationAppContext;

    public void init(ApplicationContext context) throws ServerException, MappingException {
        this.annotationAppContext = (AnnotationApplicationContext) context;
        // 初始化handlerMap(将url和method对应, 将url和controller实例对应)
        handlerMapping();
        handlerMap.forEach((key, val) -> log.debug("url: {}, handler: {}", key, val));
        try {
            // 初始化异常处理类
            initExceptionHandler();
            errorMap.forEach((key, val) -> log.debug("error: {}, handler: {}", key, val));
            // 初始化拦截器
//            initInterceptor();
        } catch (AnnotationException | BeanInstantiationException e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * 加载异常处理链
     */
    public void initExceptionHandler() throws AnnotationException, BeanInstantiationException {
        Map<Class<?>, Object> beanMap = this.annotationAppContext.createBeanWithAnnotation(ControllerAdvice.class);
        for (Map.Entry<Class<?>, Object> entry : beanMap.entrySet()) {
            Method[] methods = entry.getKey().getDeclaredMethods();
            if (ArrayUtils.isEmpty(methods)) {
                continue;
            }
            for (Method method : methods) {
                ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
                if (annotation == null) {
                    continue;
                }
                Class<?> errorClass = annotation.value();
                ErrorHolder holder = new ErrorHolder();
                if (method.getParameterCount() == 0) {
                    holder.setMethod(method);
                } else {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Map<String, Integer> map = new HashMap<>(0);
                    for (int i = 0; i < parameterTypes.length; i++) {
                        Class<?> type = parameterTypes[i];
                        if (errorClass.equals(type) || errorClass.isAssignableFrom(type)) {
                            map.put(ErrorHolder.ERROR, i);
                        }
                        if (type.equals(HttpServletRequest.class)) {
                            map.put(ErrorHolder.HTTP_REQUEST, i);
                        }
                        if (type.equals(HttpServletResponse.class)) {
                            map.put(ErrorHolder.HTTP_RESPONSE, i);
                        }
                    }
                    holder.setMap(map);
                    holder.setMethod(method);
                }
                holder.setInstance(entry.getValue());
                errorMap.put(errorClass, holder);
            }
        }
    }


    /**
     * 执行启动时执行的方法链
     * @param args javac参数
     */
    public void startAppContextChain(final String[] args, final Map<String, String> propertyMap) throws Exception {
        ApplicationRunnerChain chain = new ApplicationRunnerChain(args, propertyMap);
        Object[] objects = this.annotationAppContext.getBeanWithAnnotation(Component.class);
        List<ApplicationRunner> list = new ArrayList<>(0);
        for (Object object : objects) {
            if (object instanceof ApplicationRunner) {
                list.add((ApplicationRunner) object);
            }
        }
        chain.setContexts(list);
        chain.run();
    }

    /**
     * 实现URL与方法的映射
     * 1. 获取ioc中存在@Controller注解的bean
     * 2. 获取实例中的所有方法，判断方法是否存在@RequestMapping/@GetMapping/@PostMapping/@DeleteMapping/@PutMapping等注解
     * 3. 处理实例与方法中的@RequestMapping等注解。生成完整的uri路径
     * 4. 将uri与对应的方法写入handlerMap
     * 5. 将uri与对应的实例写入controllerMap
     *
     * @throws ServerException uri重复异常|uri不符合规范
     */
    private void handlerMapping() throws ServerException, MappingException {
        Object[] controllers = this.annotationAppContext.getBeanWithAnnotation(Controller.class);
        for (Object controller : controllers) {
            Class<?> clazz = controller.getClass();
            // 获取父级URL
            RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
            String parentUri = "";
            if (requestMapping != null) {
                parentUri = requestMapping.value();
                if (!Utils.matchUrl(parentUri, false)) {
                    throw new ServerException("controller[" + clazz + "]: url[" + parentUri + "] is no specification ..");
                }
            }
            // 获取当前类的所有声明方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                handlerMethod(controller, method, parentUri);
            }
        }
    }

    private void handlerMethod(Object instance, Method method, String parentUri) throws MappingException, ServerException {
        log.debug("----------开始映射方法----------");
        // 过滤非public方法
        if (!Modifier.isPublic(method.getModifiers())) {
            return;
        }
        // 获取url处理对象
        HandlerHolder handler = getMethodHandler(method);
        String subUri = handler.getUrl();

        if (subUri == null) {
            return;
        }

        if (!Utils.matchUrl(subUri, true)) {
            throw new ServerException("method[" + method + "]: url[" + subUri + "] is no specification ..");
        }
        // 获取子级URL
        // 判断完整的url是否已经存在
        // URL -> method
        String url = parentUri + subUri;
        if (handlerMap.containsKey(url)) {
            throw new ServerException("url[" + url + "] is exists");
        }
        Pattern compile = Pattern.compile("\\{\\w+}");
        String[] array = url.split("/");
        Map<String, Integer> map = new HashMap<>(0);
        for (int i = 0; i < array.length; i++) {
            Matcher matcher = compile.matcher(array[i]);
            int count = 0;
            while (matcher.find()) {
                count++;
                if (count >= 2) {
                    throw new ServerException("method[" + method + "]: url[" + subUri + "] is no specification ..");
                }
                map.put(matcher.group().replace("{", "").replace("}", ""), i);
            }
        }
        handler.setUrl(url);
        // 校验路径变量是否正确
        handler.setPathVariables(parsePathVariable(method, map));
        handler.setInstance(instance);
        handler.setMethod(method);
        handlerMap.put(url, handler);
        log.debug("handler: {}", handler);
        log.debug("----------映射方法完毕！----------");
    }

    /**
     * 判断方法的参数列表中存在@PathVariable的注解是否与资源路径uri匹配
     * - 获取编译class的方法参数名称使用asm框架或者jdk1.8在编译时添加 –parameters 参数选项
     * 0. map: 方法参数位置 -> 路径变量 一对一
     * 1. 判断是否存在路径变量 不存在返回 empty map
     * 2. 获取参数列表 遍历判断参数是否带有@PathVariable 注解
     * 3. 获取@PathVariable的name=null则将name设置为参数名称
     * 4. 判断 name 是否在 pathVars中 不存在则抛出异常 存在则加入到map中
     * @param method   方法
     * @param pathVars 路径变量集合
     * @return 是否匹配
     */
    private Map<Integer, Integer> parsePathVariable(Method method, Map<String, Integer> pathVars) throws MappingException {
        Map<Integer, Integer> map = new HashMap<>(0);
        Parameter[] parameters = method.getParameters();
        String[] paramNames = TypeUtils.getMethodParamNames(method);
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(PathVariable.class)) {
                String name = parameters[i].getAnnotation(PathVariable.class).value();
                if (StringUtils.isBlank(name)) {
                    name = paramNames[i];
                }
                if (!pathVars.containsKey(name)) {
                    throw new MappingException("method[" + method + "]：" + name + " is not path var");
                }
                // url以'/'切片的数组index -> 方法参数index
                map.put(pathVars.get(name), i);
            }
        }
        return map;
    }

    private void hasMappingException(Method method, boolean flag) throws MappingException {
        if (flag) {
            throw new MappingException(method + " cannot have more than two mapping annotations. You can set the url using the ant style");
        }
    }

    private HandlerHolder getMethodHandler(final Method method) throws MappingException {
        boolean flag = false;
        HandlerHolder handler = new HandlerHolder();
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        if (mapping != null) {
            flag = true;
            HttpRequestMethod[] httpRequestMethods = mapping.methods();
            if (ArrayUtils.isEmpty(httpRequestMethods)) {
                httpRequestMethods = HttpRequestMethod.values();
            }
            handler.setHttpRequestMethod(httpRequestMethods);
            handler.setUrl(mapping.value());
        }
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            hasMappingException(method, flag);
            flag = true;
            handler.setHttpRequestMethod(HttpRequestMethod.GET);
            handler.setUrl(getMapping.value());
        }
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            hasMappingException(method, flag);
            flag = true;
            handler.setHttpRequestMethod(HttpRequestMethod.POST);
            handler.setUrl(postMapping.value());
        }
        PutMapping putMapping = method.getAnnotation(PutMapping.class);
        if (putMapping != null) {
            hasMappingException(method, flag);
            flag = true;
            handler.setHttpRequestMethod(HttpRequestMethod.PUT);
            handler.setUrl(putMapping.value());
        }
        DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
        if (deleteMapping != null) {
            hasMappingException(method, flag);
            handler.setHttpRequestMethod(HttpRequestMethod.DELETE);
            handler.setUrl(deleteMapping.value());
        }
        return handler;
    }

    private FlyBootContext() {
    }

    public static FlyBootContext getInstance() {
        return Singleton.INSTANCE.getSingleton();
    }

    private enum Singleton {
        INSTANCE;
        private FlyBootContext singleton;

        Singleton() {
            this.singleton = new FlyBootContext();
        }

        public FlyBootContext getSingleton() {
            return this.singleton;
        }
    }
}
