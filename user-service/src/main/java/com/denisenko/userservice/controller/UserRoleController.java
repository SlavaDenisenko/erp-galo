package com.denisenko.userservice.controller;

import com.denisenko.userservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-roles")
@RequiredArgsConstructor
public class UserRoleController {
    private final RoleService roleService;

    @PutMapping("/{userId}/assign/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public void assignRoleToUser(@PathVariable String userId, @PathVariable String roleId) {
        roleService.assignRoleToUser(userId, roleId);
    }

    @DeleteMapping("/{userId}/remove/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeRoleFromUser(@PathVariable String userId, @PathVariable String roleId) {
        roleService.removeRoleFromUser(userId, roleId);
    }
}
