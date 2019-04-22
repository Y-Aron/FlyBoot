package org.aron.boot.annotation.web.bind;

import java.lang.annotation.*;

/**
 * @author: Y-Aron
 * @create: 2019-01-03 13:24
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {
    String value();
}
