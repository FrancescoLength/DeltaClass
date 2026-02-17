package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

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
      entity.capacity = warehouse.capacity;
      entity.stock = warehouse.stock;
      entity.location = warehouse.location;
      // potentially update other fields
    }
  }

  @Override
  @jakarta.transaction.Transactional
  public void remove(Warehouse warehouse) {
    this.delete("businessUnitCode", warehouse.businessUnitCode);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    DbWarehouse entity = find("businessUnitCode", buCode).firstResult();
    return entity != null ? entity.toWarehouse() : null;
  }

  @Override
  public long countByLocation(String location) {
    return count("location", location);
  }
}
