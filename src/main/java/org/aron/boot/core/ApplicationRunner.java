package org.aron.boot.core;

import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-02-17 19:41
 */
public interface ApplicationRunner {

    int getOrder();

    void run(String[] args, Map<String, String> propertyMap) throws Exception;
}
