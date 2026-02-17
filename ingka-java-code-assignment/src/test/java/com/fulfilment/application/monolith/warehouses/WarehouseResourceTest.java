package com.fulfilment.application.monolith.warehouses;

import com.warehouse.api.beans.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class WarehouseResourceTest {

    @Test
    public void testCreateWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MW-001");
        warehouse.setLocation("AMSTERDAM-001"); // maxWH=5 (only 1 used), maxCapacity=100
        warehouse.setCapacity(50);
        warehouse.setStock(10);

        given()
                .contentType(ContentType.JSON)
                .body(warehouse)
                .when().post("/warehouse")
                .then()
                .statusCode(200)
                .body("businessUnitCode", is("MW-001"));
    }

    @Test
    public void testCreateDuplicateWarehouse() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MW-002");
        warehouse.setLocation("AMSTERDAM-002"); // maxWH=3 (empty), maxCapacity=75
        warehouse.setCapacity(30);
        warehouse.setStock(10);

        // First create
        given()
                .contentType(ContentType.JSON)
                .body(warehouse)
                .when().post("/warehouse")
                .then()
                .statusCode(200);

        // Second create (should fail - duplicate business unit code)
        given()
                .contentType(ContentType.JSON)
                .body(warehouse)
                .when().post("/warehouse")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateWarehouseInvalidLocation() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MW-003");
        warehouse.setLocation("INVALID-LOC");
        warehouse.setCapacity(100);
        warehouse.setStock(10);

        given()
                .contentType(ContentType.JSON)
                .body(warehouse)
                .when().post("/warehouse")
                .then()
                .statusCode(400);
    }

    @Test
    public void testCreateWarehouseExceedsCapacity() {
        Warehouse warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("MW-004");
        warehouse.setLocation("EINDHOVEN-001"); // maxWH=2 (empty), maxCapacity=70
        warehouse.setCapacity(10);
        warehouse.setStock(100); // Stock > Capacity

        given()
                .contentType(ContentType.JSON)
                .body(warehouse)
                .when().post("/warehouse")
                .then()
                .statusCode(400);
    }
}
