package org.aron.boot.annotation.web.servlet;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-09 16:38
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {
    Class<?> value() default Throwable.class;
}
