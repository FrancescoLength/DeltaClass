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
                .when().get("/stores")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3)); // 3 seeded stores
    }

    @Test
    public void testGetSingleStore() {
        given()
                .when().get("/stores/1")
                .then()
                .statusCode(200)
                .body("name", is("HAARLEM"));
    }

    @Test
    public void testGetNonExistentStore() {
        given()
                .when().get("/stores/99999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateStore() {
        String payload = "{\"name\": \"ROTTERDAM-TEST\", \"quantityProductsInStock\": 7}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().post("/stores")
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
                .when().post("/stores")
                .then()
                .statusCode(422);
    }

    @Test
    public void testUpdateStore() {
        // Update store 2 (AMSTERDAM)
        String payload = "{\"name\": \"AMSTERDAM-UPDATED\", \"quantityProductsInStock\": 99}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().put("/stores/2")
                .then()
                .statusCode(200)
                .body("name", is("AMSTERDAM-UPDATED"));
    }

    @Test
    public void testUpdateNonExistentStore() {
        String payload = "{\"name\": \"GHOST\", \"quantityProductsInStock\": 1}";

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().put("/stores/99999")
                .then()
                .statusCode(404);
    }
}
