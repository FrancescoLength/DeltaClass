package com.fulfilment.application.monolith.fulfillment.adapters.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fulfillment")
public class DbFulfillment {

    @Id
    @GeneratedValue
    public Long id;

    public String productName;
    public String storeName;
    public String warehouseBusinessUnitCode;

    public DbFulfillment() {
    }

    public DbFulfillment(String productName, String storeName, String warehouseBusinessUnitCode) {
        this.productName = productName;
        this.storeName = storeName;
        this.warehouseBusinessUnitCode = warehouseBusinessUnitCode;
    }
}
