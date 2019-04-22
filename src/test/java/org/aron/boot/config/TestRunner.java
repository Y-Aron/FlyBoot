package org.aron.boot.config;

import lombok.extern.slf4j.Slf4j;
import org.aron.boot.core.ApplicationRunner;
import org.aron.context.annotation.component.Component;

import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-02-17 20:07
 */
@Component
@Slf4j
public class TestRunner implements ApplicationRunner {

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public void run(String[] args, Map<String, String> propertyMap) {

    }
}
