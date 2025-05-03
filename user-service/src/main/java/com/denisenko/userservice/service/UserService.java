package com.denisenko.userservice.service;

import com.denisenko.userservice.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class UserService {
    private final RealmResource realmResource;
    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:user:";

    public UserService(Keycloak keycloak, @Value("${keycloak.realm}") String realm, StringRedisTemplate redisTemplate) {
        this.realmResource = keycloak.realm(realm);
        this.redisTemplate = redisTemplate;
    }

    private UsersResource getUsersResource() {
        return realmResource.users();
    }

    public UserDto createUser(UserDto userDto, String idempotencyKey) {
        String existsUserId = redisTemplate.opsForValue().get(IDEMPOTENCY_PREFIX + idempotencyKey);
        if (existsUserId != null) return getUser(existsUserId);

        log.info("Creating user with username '{}'", userDto.getUsername());
        UserRepresentation user = mapToRepresentation(userDto);
        getUsersResource().create(user);
        String userId = getUsersResource().search(user.getUsername()).get(0).getId();
        if (userDto.getPassword() != null)
            setPassword(userId, userDto.getPassword());

        userDto.setId(userId);
        redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, userId, Duration.ofMinutes(10));
        return userDto;
    }

    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return getUsersResource().list().stream()
                .map(this::mapToDto)
                .toList();
    }

    public UserDto getUser(String userId) {
        log.info("Getting user with ID = {}", userId);
        UserRepresentation user = getUsersResource().get(userId).toRepresentation();
        return mapToDto(user);
    }

    public UserDto updateUser(String userId, UserDto userDto) {
        log.info("Updating user with ID = {} and username '{}'", userId, userDto.getUsername());
        UserRepresentation user = getUsersResource().get(userId).toRepresentation();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        getUsersResource().get(userId).update(user);
        return mapToDto(user);
    }

    public void deleteUser(String userId) {
        getUsersResource().get(userId).remove();
    }

    private void setPassword(String userId, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

        getUsersResource().get(userId).resetPassword(passwordCred);
    }

    private UserDto mapToDto(UserRepresentation user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    private UserRepresentation mapToRepresentation(UserDto userDto) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setEnabled(true);
        return user;
    }
}
