package it.controller;

import it.pojo.User;
import it.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService UserService;

    //登录路径
    @PostMapping("/login")
    public String login(@RequestParam String id, @RequestParam String password) {
        User user = UserService.login(id, password);
        if (user != null) {
            return "欢迎使用 " ;
        } else {
            return "错误，无法登录";
        }
    }
}
