package org.aron.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.aron.boot.annotation.web.bind.GetMapping;
import org.aron.boot.annotation.web.bind.RequestMapping;
import org.aron.boot.annotation.web.request.PathVariable;
import org.aron.boot.annotation.web.request.RequestBody;
import org.aron.boot.annotation.web.request.RequestParam;
import org.aron.boot.mapper.entity.User;
import org.aron.boot.service.TestService;
import org.aron.context.annotation.component.Autowired;
import org.aron.context.annotation.component.Controller;
import org.aron.server.servlet.http.HttpServletRequest;

import java.util.List;


@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService service;

    public TestController() {
        log.debug("test controller init");
    }

    @GetMapping("/mapping")
    public void test1(HttpServletRequest request, List name, @RequestParam final String number) {
        log.debug("-----test mapping-----");
        log.debug("request: {}", request);
        log.debug("number: {}", number);
        log.debug("name: {}", name);
    }

    @GetMapping("/save/{aaa}/{bbb}")
    public void save(@PathVariable("aaa") String aaa, @PathVariable String bbb) {
        log.debug("test controller save");
        service.save();
    }

    @GetMapping("/save")
    public void save1(String aaa, Object bbb) {
        log.debug("test controller save");
        service.save();
    }

    @GetMapping("/result")
    public Object test2() {
        User user = new User();
        user.setUsername("test");
        user.setNickname("中国姓名");
        user.setPassword(".....");
        return user;
    }

    @GetMapping("/pathVars/{name}/{id}")
    public Object test3(@PathVariable String name, @PathVariable int id) {
        log.debug("name: {}", name);
        log.debug("id: {}", id);
        return null;
    }
    @GetMapping("/json")
    public Object test4(@RequestBody String name, @RequestBody int id) {
        log.debug("name: {}", name);
        log.debug("id: {}", id);
        return null;
    }

}
