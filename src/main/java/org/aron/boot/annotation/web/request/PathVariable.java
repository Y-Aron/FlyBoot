package org.aron.boot.annotation.web.request;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-03 18:47
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {
    String value() default "";
}
