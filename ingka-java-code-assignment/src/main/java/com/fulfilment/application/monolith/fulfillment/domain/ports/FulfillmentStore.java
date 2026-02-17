package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;

public interface FulfillmentStore {
    void save(Fulfillment fulfillment);

    long countWarehousesByProductAndStore(String productName, String storeName);

    long countUniqueWarehousesByStore(String storeName);

    long countUniqueProductsByWarehouse(String warehouseBusinessUnitCode);

    boolean exists(String productName, String storeName, String warehouseBusinessUnitCode);

    boolean isWarehouseAssociatedWithStore(String warehouseCode, String storeName);

    boolean isProductAssociatedWithWarehouse(String productName, String warehouseCode);
}
