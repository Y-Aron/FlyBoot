package org.aron.boot.annotation.web.response;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-03 18:40
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
    String value() default "";
}
