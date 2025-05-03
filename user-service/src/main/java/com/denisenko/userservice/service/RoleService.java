package com.denisenko.userservice.service;

import com.denisenko.userservice.config.KeycloakConfig;
import com.denisenko.userservice.dto.RoleDto;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RoleService {
    private final RealmResource realmResource;
    private final ClientResource clientResource;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:role:";

    public RoleService(Keycloak keycloak, KeycloakConfig keycloakConfig, StringRedisTemplate redisTemplate) {
        this.realmResource = keycloak.realm(keycloakConfig.getRealm());
        this.clientResource = this.realmResource.clients().get(getClientUUID(keycloakConfig.getClientId()));
        this.redisTemplate = redisTemplate;
    }

    private String getClientUUID(String clientId) {
        return realmResource.clients().findByClientId(clientId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Client not found"))
                .getId();
    }

    public RoleDto createRole(RoleDto roleDto, String idempotencyKey) {
        String roleName = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (roleName != null) return getRole(roleName);

        log.info("Creating role with name '{}'", roleDto.getName());
        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());
        try {
            clientResource.roles().create(role);
        } catch (Exception e) {
            log.error("Error creating role", e);
            throw new RuntimeException("Can't create role: " + e.getMessage());
        }
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, role.getName(), Duration.ofMinutes(10));
        return getRole(role.getName());
    }

    public List<RoleDto> getAllRoles() {
        log.info("Getting all roles");
        return clientResource.roles().list().stream()
                .map(this::mapToDto)
                .toList();
    }

    private RoleDto mapToDto(RoleRepresentation role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }

    public RoleDto getRole(String roleName) {
        RoleRepresentation role = clientResource.roles().get(roleName).toRepresentation();
        return mapToDto(role);
    }

    public RoleDto updateRole(String roleId, RoleDto roleDto) {
        log.info("Updating role with ID = {}", roleId);
        RoleRepresentation role = getRoleRepresentation(roleId);
        role.setDescription(roleDto.getDescription());
        clientResource.roles().get(role.getName()).update(role);
        return mapToDto(role);
    }

    public void deleteRole(String roleId) {
        log.warn("Deleting role with ID = {}", roleId);
        RoleRepresentation role = getRoleRepresentation(roleId);
        clientResource.roles().get(role.getName()).remove();
    }

    public void assignRoleToUser(String userId, String roleId) {
        log.info("Assigning role with ID = {} to user with ID = {}", roleId, userId);
        RoleRepresentation role = getRoleRepresentation(roleId);
        realmResource.users()
                .get(userId)
                .roles()
                .clientLevel(clientResource.toRepresentation().getId())
                .add(Collections.singletonList(role));
    }

    public void removeRoleFromUser(String userId, String roleId) {
        log.info("Removing role with ID = {} from user with ID = {}", roleId, userId);
        RoleRepresentation role = getRoleRepresentation(roleId);
        realmResource.users()
                .get(userId)
                .roles()
                .clientLevel(clientResource.toRepresentation().getId())
                .remove(Collections.singletonList(role));
    }

    private RoleRepresentation getRoleRepresentation(String roleId) {
        return clientResource.roles().list().stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleId));
    }
}
