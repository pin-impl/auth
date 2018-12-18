package com.icl.authentication.user;

import com.icl.authentication.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("list")
    public List<User> selectUser(User user) {
        log.info("list users. param: {}", user.toString());
        return userService.listUsers(user);
    }

    @GetMapping("count")
    public int userCount(User user) {
        log.info("count user. user={}", user.toString());
        return userService.countUser(user);
    }
}
