package org.aron.boot.error.holder;

import lombok.Setter;
import lombok.ToString;
import org.aron.server.servlet.http.HttpServletRequest;
import org.aron.server.servlet.http.HttpServletResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Y-Aron
 * @create 2019/4/2
 */
@ToString
public class ErrorHolder {

    public static final String ERROR = "ERROR";
    public static final String HTTP_REQUEST = "http_request";
    public static final String HTTP_RESPONSE = "http_response";

    @Setter
    private Method method;

    @Setter
    private Object instance;

    @Setter
    private Map<String, Integer> map;

    public void handlerError(HttpServletRequest request, HttpServletResponse response, Exception e) throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[method.getParameterCount()];
        args[map.get(ERROR)] = e;
        args[map.get(HTTP_REQUEST)] = request;
        args[map.get(HTTP_RESPONSE)] = response;
        method.invoke(instance, args);
    }
}
