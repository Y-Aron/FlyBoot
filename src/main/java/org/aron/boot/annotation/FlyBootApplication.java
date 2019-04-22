package org.aron.boot.annotation;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-09 16:38
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlyBootApplication {
    // 扫描的包路径
    String[] scanPackages() default {};

    // 过滤的包路径
    String[] filterPackages() default {};

    // 开启自动加载配置
    boolean loadConfig() default true;
}
