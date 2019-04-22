package org.aron.boot.servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aron.boot.annotation.web.request.RequestBody;
import org.aron.boot.annotation.web.request.RequestParam;
import org.aron.boot.core.FlyBootContext;
import org.aron.boot.core.holder.HandlerHolder;
import org.aron.boot.error.holder.ErrorHolder;
import org.aron.commons.constant.CharsetConstant;
import org.aron.commons.utils.TypeUtils;
import org.aron.commons.utils.Utils;
import org.aron.server.annotation.WebServlet;
import org.aron.server.core.ServerConfiguration;
import org.aron.server.error.ServletException;
import org.aron.server.servlet.ServletRequest;
import org.aron.server.servlet.ServletResponse;
import org.aron.server.servlet.http.HttpServlet;
import org.aron.server.servlet.http.HttpServletRequest;
import org.aron.server.servlet.http.HttpServletResponse;
import org.aron.server.servlet.http.enumeration.HttpStatus;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.aron.server.servlet.constant.HttpConstant.CLOSE_KEY;
import static org.aron.server.servlet.constant.HttpConstant.CLOSE_VALUE;

/**
 * @author: Y-Aron
 * @create: 2019-02-10 21:09
 */
@Slf4j
@WebServlet("/**")
public class DispatcherServlet extends HttpServlet {

    private FlyBootContext context;

    @Override
    public void init(ServerConfiguration config) {
        context = FlyBootContext.getInstance();
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取请求URL
        String url = request.getUrl();
        try {
            doDispatcher(url, request, response);
        } catch (ServletException e) {
            response.setHeader(CLOSE_KEY, CLOSE_VALUE);
            throw e;
        } catch (Exception e) {
            if (!doHandlerError(e, request, response)) {
                throw new ServletException(HttpStatus.INTERNAL_SERVER_ERROR, Utils.stackTraceToString(e));
            }
        }
    }

    private boolean doHandlerError(Exception error, HttpServletRequest request, HttpServletResponse response) {
        Map<Class<?>, ErrorHolder> errorMap = this.context.getErrorMap();
        Class<?> target = error.getClass();
        for (Map.Entry<Class<?>, ErrorHolder> entry : errorMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            if (target.equals(clazz) || clazz.isAssignableFrom(target)) {
                try {
                    entry.getValue().handlerError(request, response, error);
                    return true;
                } catch (InvocationTargetException | IllegalAccessException e) {
                    return false;
                }
            }
        }
        return false;
    }

    private void doDispatcher(String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 匹配URL对应的 Handler
        HandlerHolder handler = mapHandler(url);
        if (handler == null) {
            throw new ServletException(HttpStatus.NOT_FOUND);
        }
        if (!handler.hasHttpRequestMethod(request.getMethod())) {
            throw new ServletException(HttpStatus.NOT_FOUND);
        }
        // 处理方法参数映射
        Object[] objects = getMethodParameter(handler, request, response);

        // 执行方法 处理响应数据
        Object result;
        result = handler.getMethod().invoke(handler.getInstance(), objects);

        if (result == null) {
            response.write("");
            return;
        }
        String resp;
        // 自动判断响应数据是否存在中文
        // 存在则自动处理中文乱码问题
        if (TypeUtils.isBaseType(result)) {
            resp = String.valueOf(result);
            // 基本类型
            if (Utils.isContainChinese(resp)) {
                response.setCharacterEncoding(CharsetConstant.UTF_8);
            }
        } else {
            // 其他类型
            resp = JSON.toJSONString(result);
            if (Utils.isContainChinese(resp)) {
                response.setCharacterEncoding(CharsetConstant.UTF_8);
            }
        }
        response.write(resp);
    }

    private HandlerHolder mapHandler(String url) {
        HandlerHolder handler = context.getHandlerMap().get(url);
        if (handler != null) {
            return handler;
        }
        Collection<HandlerHolder> handlerHolders = context.getHandlerMap().values();
        String[] urlArray = url.split("/");
        for (HandlerHolder handlerHolder : handlerHolders) {
            String uri = handlerHolder.getUrl();
            if (uri.split("/").length != urlArray.length) {
                continue;
            }
            String[] uriArray = uri.split("/");
            Set<Integer> keySet = handlerHolder.getPathVariables().keySet();
            boolean accept = true;
            for (int i = 0; i < urlArray.length; i++) {
                if (!keySet.contains(i) && !StringUtils.equals(uriArray[i], urlArray[i])) {
                    accept = false;
                    break;
                }
            }
            if (accept) {
                handler = handlerHolder;
                handler.setUrlArray(urlArray);
            }
        }
        return handler;
    }

    private Object[] getMethodParameter(HandlerHolder handler, HttpServletRequest request, HttpServletResponse response) {
        Method method = handler.getMethod();
        Parameter[] parameters = method.getParameters();
        if (ArrayUtils.isEmpty(parameters)) {
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        Object[] parameterValues = new Object[parameters.length];
        JSONObject jsonObject = request.getJsonBody();
        String[] paramNames = TypeUtils.getMethodParamNames(method);
        for (int i = 0; i < parameters.length; i++) {
            Class<?> type = parameters[i].getType();
            if (type.equals(HttpServletRequest.class)) {
                parameterValues[i] = request;
                continue;
            }
            if (type.equals(HttpServletResponse.class)) {
                parameterValues[i] = response;
                continue;
            }
            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                // 表单参数
                String name = parameters[i].getAnnotation(RequestParam.class).value();
                if (StringUtils.isBlank(name)) {
                    name = paramNames[i];
                }
                if (List.class.isAssignableFrom(type)) {
                    parameterValues[i] = request.getParameters(name);
                }
                parameterValues[i] = TypeUtils.convertStringToObject(request.getParameter(name), type);
                continue;
            }
            if (parameters[i].isAnnotationPresent(RequestBody.class)) {
                // JSON参数
                if (jsonObject == null) {
                    continue;
                }
                String name = parameters[i].getAnnotation(RequestBody.class).value();
                if (StringUtils.isBlank(name)) {
                    name = paramNames[i];
                }
                if (jsonObject.containsKey(name)) {
                    parameterValues[i] = jsonObject.getObject(name, type);
                    continue;
                }
            }
            parameterValues[i] = TypeUtils.convertStringToObject(null, type);
        }
        Set<Map.Entry<Integer, Integer>> entries = handler.getPathVariables().entrySet();
        String[] urlArray = handler.getUrlArray();
        for (Map.Entry<Integer, Integer> entry : entries) {
            // index -> i
            int key = entry.getKey();
            int val = entry.getValue();
            parameterValues[val] = TypeUtils.convertStringToObject(urlArray[key], parameters[val].getType());
        }
        return parameterValues;
    }
}
