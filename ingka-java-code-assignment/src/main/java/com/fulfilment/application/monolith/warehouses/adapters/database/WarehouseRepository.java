package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  @jakarta.transaction.Transactional
  public void create(Warehouse warehouse) {
    DbWarehouse entity = new DbWarehouse();
    entity.businessUnitCode = warehouse.businessUnitCode;
    entity.location = warehouse.location;
    entity.capacity = warehouse.capacity;
    entity.stock = warehouse.stock;
    entity.createdAt = java.time.LocalDateTime.now();
    this.persist(entity);
  }

  @Override
  @jakarta.transaction.Transactional
  public void update(Warehouse warehouse) {
    DbWarehouse entity = find("businessUnitCode", warehouse.businessUnitCode).firstResult();
    if (entity != null) {
      entity.location = warehouse.location;
      entity.capacity = warehouse.capacity;
      entity.stock = warehouse.stock;
      // Preserve creation date
    }
  }

  @Override
  @jakarta.transaction.Transactional
  public void remove(Warehouse warehouse) {
    delete("businessUnitCode", warehouse.businessUnitCode);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse entity = find("businessUnitCode", buCode).firstResult();
    if (entity == null) {
      return null;
    }
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = entity.businessUnitCode;
    warehouse.location = entity.location;
    warehouse.capacity = entity.capacity;
    warehouse.stock = entity.stock;
    if (entity.createdAt != null) {
      warehouse.creationAt = entity.createdAt.atZone(java.time.ZoneId.systemDefault());
    }
    if (entity.archivedAt != null) {
      warehouse.archivedAt = entity.archivedAt.atZone(java.time.ZoneId.systemDefault());
    }
    return warehouse;
  }

  @Override
  public java.util.List<Warehouse> getAll() {
    return listAll().stream().map(entity -> {
      Warehouse warehouse = new Warehouse();
      warehouse.businessUnitCode = entity.businessUnitCode;
      warehouse.location = entity.location;
      warehouse.capacity = entity.capacity;
      warehouse.stock = entity.stock;
      if (entity.createdAt != null) {
        warehouse.creationAt = entity.createdAt.atZone(java.time.ZoneId.systemDefault());
      }
      if (entity.archivedAt != null) {
        warehouse.archivedAt = entity.archivedAt.atZone(java.time.ZoneId.systemDefault());
      }
      return warehouse;
    }).collect(java.util.stream.Collectors.toList());
  }
}
