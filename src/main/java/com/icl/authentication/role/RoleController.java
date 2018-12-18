package com.icl.authentication.role;

import com.icl.authentication.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("list")
    public List<Role> listRole(Role role) {
        log.info("list roles . param: {}", role.toString());
        return roleService.listRole(role);
    }

    @GetMapping("count")
    public int countRole(Role role) {
        log.info("count role. param: {}", role.toString());
        return roleService.countRole(role);
    }

    @PostMapping("update")
    public Role updateRole(@RequestBody Role role) {
        log.info("update role. param: {}", role.toString());
        return roleService.updateRole(role);
    }

    @PutMapping("add")
    public Role addRole(@RequestBody Role role) {
        log.info("add role. param: {}", role.toString());
        return roleService.addRole(role);
    }

    @DeleteMapping("{id}/delete")
    public Boolean dropRole(@PathVariable("id") long id) {
        log.info("delete role. id : {}", id);
        return roleService.dropRole(id);
    }
}
