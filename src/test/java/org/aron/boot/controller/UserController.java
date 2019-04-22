package org.aron.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.aron.boot.annotation.web.bind.PostMapping;
import org.aron.boot.annotation.web.request.RequestParam;
import org.aron.context.annotation.component.Controller;

/**
 * @author: Y-Aron
 * @create: 2019-02-17 00:24
 */
@Slf4j
@Controller
public class UserController {

    @PostMapping("/login")
    public void login(@RequestParam String username, @RequestParam String password) {
        log.debug("username: {}", username);
        log.debug("password: {}", password);
//        Subject subject = SecurityUtils.getSubject();
//        UsernamePasswordToken token = new UsernamePasswordToken("admin", "1234");
//        try {
//            subject.login(token);
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.getMessage());
//        }
//        log.debug("subject: {}", subject);
    }
}
