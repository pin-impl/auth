package com.icl.authentication.role;

import com.icl.authentication.common.mapper.RoleMapper;
import com.icl.authentication.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    public List<Role> listRole(Role role) {
        role.setDeleted(false);
        return roleMapper.select(role);
    }

    public int countRole(Role role) {
        role.setDeleted(false);
        return roleMapper.selectCount(role);
    }

    public Role updateRole(Role role) {
        role.setUpdateTime(new Date());
        roleMapper.updateByPrimaryKeySelective(role);
        return role;
    }

    public Boolean dropRole(long id) {
        Role role = new Role();
        role.setDeleted(true).setId(id);
        return roleMapper.updateByPrimaryKeySelective(role) == 1;
    }

    public Role addRole(Role role) {
        role.setDeleted(false).setCreateTime(new Date()).setUpdateTime(new Date());
        roleMapper.insertUseGeneratedKeys(role);
        return role;
    }
}
