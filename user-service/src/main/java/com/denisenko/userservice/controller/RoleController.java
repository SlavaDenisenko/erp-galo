package com.denisenko.userservice.controller;

import com.denisenko.userservice.dto.RoleDto;
import com.denisenko.userservice.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody @Valid RoleDto roleDto,
                                              @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return new ResponseEntity<>(roleService.createRole(roleDto, idempotencyKey), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleDto> updateRole(@PathVariable String roleId, @RequestBody @Valid RoleDto roleDto) {
        return new ResponseEntity<>(roleService.updateRole(roleId, roleDto), HttpStatus.OK);
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable String roleId) {
        roleService.deleteRole(roleId);
    }
}
