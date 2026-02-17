package com.fulfilment.application.monolith.fulfillment.domain.models;

public class Fulfillment {
    public String productName;
    public String storeName;
    public String warehouseBusinessUnitCode;

    public Fulfillment() {
    }

    public Fulfillment(String productName, String storeName, String warehouseBusinessUnitCode) {
        this.productName = productName;
        this.storeName = storeName;
        this.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    }
}
