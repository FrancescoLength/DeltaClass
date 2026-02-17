package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class StoreResourceTest {

    @Test
    public void testListStores() {
        given()
                .when().get("/store")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3)); // 3 seeded stores
    }

    @Test
    public void testGetSingleStore() {
        given()
                .when().get("/store/1")
                .then()
                .statusCode(200)
                .body("name", is("TONSTAD"));
    }

    @Test
    public void testGetNonExistentStore() {
        given()
                .when().get("/store/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateStore() {
        String payload = "{\"name\": \"ROTTERDAM-TEST\", \"quantityProductsInStock\": 7}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/store")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", is("ROTTERDAM-TEST"));
    }

    @Test
    public void testCreateStoreWithId_ShouldFail() {
        String payload = "{\"id\": 999, \"name\": \"BAD-STORE\", \"quantityProductsInStock\": 1}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/store")
                .then()
                .statusCode(422);
    }

    @Test
    public void testUpdateStore() {
        // Update store 2 (KALLAX)
        String payload = "{\"name\": \"KALLAX-UPDATED\", \"quantityProductsInStock\": 99}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().put("/store/2")
                .then()
                .statusCode(200)
                .body("name", is("KALLAX-UPDATED"));
    }

    @Test
    public void testUpdateNonExistentStore() {
        String payload = "{\"name\": \"GHOST\", \"quantityProductsInStock\": 1}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().put("/store/99999")
                .then()
                .statusCode(404);
    }
}
