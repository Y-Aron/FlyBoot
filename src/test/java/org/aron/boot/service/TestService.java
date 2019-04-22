package org.aron.boot.service;

import lombok.extern.slf4j.Slf4j;
import org.aron.boot.dao.TestDao;
import org.aron.context.annotation.component.Autowired;
import org.aron.context.annotation.component.Service;


@Slf4j
@Service
public class TestService {

    @Autowired
    private TestDao dao;

    public TestService() {
        log.debug("service init");
    }

    public void save() {
        log.debug("service save");
        dao.save();
    }
}
