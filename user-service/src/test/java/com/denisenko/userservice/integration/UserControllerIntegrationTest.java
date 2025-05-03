package com.denisenko.userservice.integration;

import com.denisenko.userservice.dto.UserDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(ContainerConfig.class)
public class UserControllerIntegrationTest {

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    static final String GRANT_TYPE_PASSWORD = "password";
    static final String CLIENT_ID = "test-client";
    static final String CLIENT_SECRET = "9uNx6hi8kRf6hAVfbbMiWbgVJ7o8L7k1";

    @LocalServerPort
    private int port;

    @Autowired
    OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void shouldGetUnauthorizedWhenGetUsers() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldGetForbiddenWhenGetUsers() {
        String token = getAccessToken("egor", "super_password");
        given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void shouldCreateUserWithAuthToken() {
        String token = getAccessToken("slava", "admin_password");
        Response response = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .body(createUser())
                .when()
                .post("/users")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .response();
        UserDto createdUser = response.as(UserDto.class);

        String authServerUrl = keycloakUrl + "/admin/realms/test-realm/users?username=" + createdUser.getUsername();
        String clientToken = getClientToken();
        Response keycloakResponse = given()
                .auth()
                .oauth2(clientToken)
                .when()
                .get(authServerUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<UserDto> receivedUser = Arrays.asList(keycloakResponse.as(UserDto[].class));
        assertThat(receivedUser).isNotNull();
        assertThat(receivedUser).hasSize(1);
        assertThat(receivedUser.get(0)).isEqualTo(createdUser);
    }

    @Test
    void shouldGetUsersWithAuthToken() {
        String token = getAccessToken("slava", "admin_password");
        Response response = given()
                .auth()
                .oauth2(token)
                .contentType(ContentType.JSON)
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        List<Map<String, Object>> users = response.jsonPath().getList("$");
        assertEquals(2, users.size());

        assertTrue(users.stream().anyMatch(user -> "slava".equals(user.get("username"))));
        assertTrue(users.stream().anyMatch(user -> "egor".equals(user.get("username"))));
    }

    private UserDto createUser() {
        return UserDto.builder()
                .username("vlada")
                .firstName("Vladislava")
                .lastName("Schmidt")
                .email("vlada@example.com")
                .password("vlada_password")
                .build();
    }

    private String getAccessToken(String username, String password) {
        String authServerUrl =
                oAuth2ResourceServerProperties.getJwt().getIssuerUri() +
                        "/protocol/openid-connect/token";

        return given()
                .contentType(ContentType.URLENC)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("username", username)
                .formParam("password", password)
                .formParam("grant_type", GRANT_TYPE_PASSWORD)
                .when()
                .post(authServerUrl)
                .then()
                .extract()
                .path("access_token");
    }

    private String getClientToken() {
        String authServerUrl =
                oAuth2ResourceServerProperties.getJwt().getIssuerUri() +
                        "/protocol/openid-connect/token";

        return given()
                .contentType(ContentType.URLENC)
                .formParam("client_id", CLIENT_ID)
                .formParam("client_secret", CLIENT_SECRET)
                .formParam("grant_type", "client_credentials")
                .when()
                .post(authServerUrl)
                .then()
                .extract()
                .path("access_token");
    }
}
