package com.denisenko;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class PrinterEmulatorResourceTest {

    @Test
    void testPrintEveryFifthFails() {
        int success = 0;
        int failures = 0;

        for (int i = 0; i < 200; i++) {
            Response response = given()
                    .contentType(ContentType.TEXT)
                    .body("Test receipt " + (i + 1))
                    .post("/printer/print");

            if (response.getStatusCode() == HttpStatus.SC_OK) {
                success++;
            } else if (response.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                failures++;
                response.then().body(containsString("Impossible to print because there is no connection"));
            }
        }

        System.out.println("Failures: " + failures + ", Success: " + success);
        assertTrue(failures >= 30 && failures <= 50, "Failures should be approximately 20% of attempts");
    }
}
