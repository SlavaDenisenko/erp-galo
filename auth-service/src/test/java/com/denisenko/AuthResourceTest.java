package com.denisenko;

import com.denisenko.dto.UserCredentials;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@QuarkusTestResource(MockKeycloakTestResource.class)
public class AuthResourceTest {

    @Test
    public void testLogin() {
        String accessToken = getAccessToken();
        System.out.println(accessToken);
    }

    @Test
    public void testVerifyRoles() {
        given()
                .header("Authorization", "Bearer " + getAccessToken())
                .header("X-Auth-Request-Redirect", "/reports/templates")
                .header("X-Original-Method", "GET")
                .when()
                .get("/auth/verify")
                .then()
                .statusCode(200)
                .header("X-User", equalTo("Vyacheslav Denis"))
                .header("X-User-Role", equalTo("MANAGER"));
    }

    private String getAccessToken() {
        return given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new UserCredentials("slava", "admin_password"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .extract()
                .path("accessToken");
    }
}
