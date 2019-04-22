package org.aron.boot.dao;

import lombok.extern.slf4j.Slf4j;
import org.aron.context.annotation.component.Resource;

@Slf4j
@Resource
public class TestDao {

    public TestDao() {
        log.debug("test dao init");
    }

    public void save() {
        log.debug("dao save");
    }
}
