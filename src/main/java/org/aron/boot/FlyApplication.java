package org.aron.boot;

import lombok.extern.slf4j.Slf4j;
import org.aron.boot.annotation.FlyBootApplication;
import org.aron.boot.core.FlyBootConfiguration;
import org.aron.boot.servlet.DispatcherServlet;
import org.aron.commons.utils.Utils;
import org.aron.context.core.impl.AnnotationApplicationContext;
import org.aron.server.ServerBootStrap;


/**
 * @author: Y-Aron
 * @create: 2019-01-02 11:01
 **/
@Slf4j
public class FlyApplication {

    private FlyApplication() {}

    public static void run(Class<?> clazz, String[] args) {
        FlyBootApplication boot = clazz.getAnnotation(FlyBootApplication.class);
        if (boot == null) {
            throw new RuntimeException(clazz.getName() + " has no @FlyBootApplication annotations");
        }
        // 0. 设置根路径
        AnnotationApplicationContext context = new AnnotationApplicationContext(clazz);
        // 1. 获取要扫描的包路径
        context.setScanPackages(boot.scanPackages());
        // 2. 获取要排除得包路径
        context.setFilterPackages(boot.filterPackages());
        // 3. 是否加载配置类
        context.isloadConfiguration(boot.loadConfig());
        try {
            // 4. 初始化上下文
            context.init();
            // 5. 配置全局servlet
            context.setBean(DispatcherServlet.class, false);
            ServerBootStrap bootStrap = ServerBootStrap.build(context);
            FlyBootConfiguration configuration = new FlyBootConfiguration();
            configuration.init(context, args);
            bootStrap.run();
        } catch (Exception e) {
            log.error(Utils.stackTraceToString(e));
            System.exit(0);
        }
    }
}
