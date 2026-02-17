package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.AssociateProductOperation;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

@ApplicationScoped
public class AssociateProductUseCase implements AssociateProductOperation {

    private final FulfillmentStore fulfillmentStore;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final WarehouseStore warehouseStore;

    @Inject
    public AssociateProductUseCase(
            FulfillmentStore fulfillmentStore,
            ProductRepository productRepository,
            StoreRepository storeRepository,
            WarehouseStore warehouseStore) {
        this.fulfillmentStore = fulfillmentStore;
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
        this.warehouseStore = warehouseStore;
    }

    @Override
    public void associate(Fulfillment fulfillment) {
        // 1. Validate existence
        validateEntitiesExist(fulfillment);

        // 2. Check if already exists
        if (fulfillmentStore.exists(
                fulfillment.productName,
                fulfillment.storeName,
                fulfillment.warehouseBusinessUnitCode)) {
            return; // Already associated
        }

        // 3. Enforce constraints
        enforceConstraints(fulfillment);

        // 4. Save
        fulfillmentStore.save(fulfillment);
    }

    private void validateEntitiesExist(Fulfillment fulfillment) {
        Product product = productRepository.find("name", fulfillment.productName).firstResult();
        if (product == null) {
            throw new ValidationException("Product not found: " + fulfillment.productName);
        }

        Store store = storeRepository.find("name", fulfillment.storeName).firstResult();
        if (store == null) {
            throw new ValidationException("Store not found: " + fulfillment.storeName);
        }

        Warehouse warehouse = warehouseStore.findByBusinessUnitCode(fulfillment.warehouseBusinessUnitCode);
        if (warehouse == null) {
            throw new ValidationException("Warehouse not found: " + fulfillment.warehouseBusinessUnitCode);
        }
    }

    private void enforceConstraints(Fulfillment fulfillment) {
        // Constraint 1: Max 2 warehouses per product per store
        long warehousesForProductInStore = fulfillmentStore.countWarehousesByProductAndStore(fulfillment.productName,
                fulfillment.storeName);
        if (warehousesForProductInStore >= 2) {
            throw new ValidationException("Product '" + fulfillment.productName
                    + "' can be fulfilled by a maximum of 2 different Warehouses per Store '" + fulfillment.storeName
                    + "'");
        }

        // Constraint 2: Max 3 warehouses per store
        long warehousesForStore = fulfillmentStore.countUniqueWarehousesByStore(fulfillment.storeName);
        if (warehousesForStore >= 3 && !warehouseAlreadyAssociatedWithStore(fulfillment)) {
            throw new ValidationException(
                    "Store '" + fulfillment.storeName + "' can be fulfilled by a maximum of 3 different Warehouses");
        }

        // Constraint 3: Max 5 types of products per warehouse
        long productsInWarehouse = fulfillmentStore
                .countUniqueProductsByWarehouse(fulfillment.warehouseBusinessUnitCode);
        if (productsInWarehouse >= 5 && !productAlreadyInWarehouse(fulfillment)) {
            throw new ValidationException("Warehouse '" + fulfillment.warehouseBusinessUnitCode
                    + "' can store maximally 5 types of Products");
        }
    }

    private boolean warehouseAlreadyAssociatedWithStore(Fulfillment f) {
        return fulfillmentStore.isWarehouseAssociatedWithStore(f.warehouseBusinessUnitCode, f.storeName);
    }

    private boolean productAlreadyInWarehouse(Fulfillment f) {
        return fulfillmentStore.isProductAssociatedWithWarehouse(f.productName, f.warehouseBusinessUnitCode);
    }
}
