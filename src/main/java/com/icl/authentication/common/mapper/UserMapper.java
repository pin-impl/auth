package com.icl.authentication.common.mapper;

import com.icl.authentication.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select * from user u where u.username = #{username}")
    User findUser(String username);
}
