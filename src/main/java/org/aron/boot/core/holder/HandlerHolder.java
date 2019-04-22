package org.aron.boot.core.holder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.aron.server.servlet.http.enumeration.HttpRequestMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: Y-Aron
 * @create: 2019-02-11 10:18
 **/
@ToString
public class HandlerHolder {

    @Setter
    @Getter
    private String url;

    @Setter
    @Getter
    private Object instance;

    @Setter
    @Getter
    private Method method;

    @Getter
    private Set<HttpRequestMethod> httpRequestMethod;

    @Setter
    @Getter
    private String[] urlArray;

    /**
     * url已'/'切片的数组index -> 方法参数index 一对一
     */
    @Setter
    @Getter
    private Map<Integer, Integer> pathVariables;

    public void setHttpRequestMethod(HttpRequestMethod... httpRequestMethod) {
        this.httpRequestMethod = new HashSet<>(0);
        this.httpRequestMethod.addAll(Arrays.asList(httpRequestMethod));
    }

    public boolean hasHttpRequestMethod(HttpRequestMethod httpRequestMethod) {
        return this.httpRequestMethod.contains(httpRequestMethod);
    }

    public String getHttpMethod() {
        StringBuilder sb = new StringBuilder();
        httpRequestMethod.forEach(method -> sb.append(method.getName()).append("/"));
        return sb.substring(0, sb.length() - 1);
    }

    public String getName() {
        return getHttpMethod() + ": [" + url + "]";
    }
}

