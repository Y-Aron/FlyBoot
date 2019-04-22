package org.aron.boot.core.impl;

import lombok.Data;
import org.aron.boot.core.ApplicationRunner;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author: Y-Aron
 * @create: 2019-02-17 19:40
 */
@Data
public class ApplicationRunnerChain {

    private List<ApplicationRunner> contexts;

    private final String[] args;

    private final Map<String, String> propertyMap;

    public void run() throws Exception {
        contexts.sort(Comparator.comparingInt(ApplicationRunner::getOrder));
        for (ApplicationRunner context : contexts) {
            context.run(args, propertyMap);
        }
    }
}
