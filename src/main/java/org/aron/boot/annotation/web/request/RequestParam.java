package org.aron.boot.annotation.web.request;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-03 13:18
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {
    String value() default "";
}
