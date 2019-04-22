package org.aron.boot.annotation.web.bind;
import org.aron.server.servlet.http.enumeration.HttpRequestMethod;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-03 13:24
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    HttpRequestMethod[] methods() default {};
    String value() default "";
}


