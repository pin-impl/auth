package com.icl.authentication.token;

import com.icl.authentication.common.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UserMapper userMapper;

    public String getUserToken(LoginRequest request) {
        userMapper.findUser(request.getUsername());
        return null;
    }

}
