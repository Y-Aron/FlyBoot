package org.aron.boot.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aron.commons.utils.PropertyUtils;
import org.aron.commons.utils.Utils;
import org.aron.context.core.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-02-10 19:49
 */
@Slf4j
public class FlyBootConfiguration {

    /**
     * 所有配置文件键值对象
     */
    @Getter
    private static Map<String, String> propertyMap;

    /**
     * 一个url对应一个method
     */
    @Getter
    private Map<String, Method> handlerMap = new HashMap<>(0);

    /**
     * 一个url对应一个controller
     */
    @Getter
    private Map<String, Object> controllerMap = new HashMap<>(0);

    @Getter
    private static FlyBootContext context;

    static {
        try {
            propertyMap = PropertyUtils.loadAll(FlyBootConfiguration.class.getResource("/").getPath());
        } catch (Exception e) {
            log.error(Utils.stackTraceToString(e));
            System.exit(0);
        }
    }

    public void init(ApplicationContext appContext, String[] args) throws Exception {
        context = FlyBootContext.getInstance();
        context.init(appContext);
        context.startAppContextChain(args, propertyMap);
    }
}
