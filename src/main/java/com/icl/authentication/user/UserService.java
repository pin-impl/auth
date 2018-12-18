package com.icl.authentication.user;

import com.icl.authentication.common.mapper.UserMapper;
import com.icl.authentication.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public List<User> listUsers(User user) {
        user.setDeleted(false);
        List<User> result =  userMapper.select(user);
        log.debug("list user: {}", result.toString());
        return result;
    }
    public int countUser(User user) {
        user.setDeleted(false);
        return userMapper.selectCount(user);
    }
}
