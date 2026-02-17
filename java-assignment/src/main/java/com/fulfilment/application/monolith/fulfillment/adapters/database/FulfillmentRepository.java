package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FulfillmentRepository implements FulfillmentStore, PanacheRepository<DbFulfillment> {

    @Override
    @Transactional
    public void save(Fulfillment fulfillment) {
        DbFulfillment entity = new DbFulfillment(
                fulfillment.productName,
                fulfillment.storeName,
                fulfillment.warehouseBusinessUnitCode);
        this.persist(entity);
    }

    @Override
    public long countWarehousesByProductAndStore(String productName, String storeName) {
        return count("productName = ?1 and storeName = ?2", productName, storeName);
    }

    @Override
    public long countUniqueWarehousesByStore(String storeName) {
        // We need unique warehouses for a store.
        // In Panache, we can use a custom query or HQL.
        return find("select count(distinct f.warehouseBusinessUnitCode) from DbFulfillment f where f.storeName = ?1",
                storeName)
                .project(Long.class)
                .singleResult();
    }

    @Override
    public long countUniqueProductsByWarehouse(String warehouseBusinessUnitCode) {
        return find("select count(distinct f.productName) from DbFulfillment f where f.warehouseBusinessUnitCode = ?1",
                warehouseBusinessUnitCode)
                .project(Long.class)
                .singleResult();
    }

    @Override
    public boolean exists(String productName, String storeName, String warehouseBusinessUnitCode) {
        return count("productName = ?1 and storeName = ?2 and warehouseBusinessUnitCode = ?3",
                productName, storeName, warehouseBusinessUnitCode) > 0;
    }

    @Override
    public boolean isWarehouseAssociatedWithStore(String warehouseCode, String storeName) {
        return count("warehouseBusinessUnitCode = ?1 and storeName = ?2", warehouseCode, storeName) > 0;
    }

    @Override
    public boolean isProductAssociatedWithWarehouse(String productName, String warehouseCode) {
        return count("productName = ?1 and warehouseBusinessUnitCode = ?2", productName, warehouseCode) > 0;
    }
}
