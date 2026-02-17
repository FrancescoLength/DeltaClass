package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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
    entity.createdAt = LocalDateTime.now();
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
      if (warehouse.archivedAt != null) {
        entity.archivedAt = warehouse.archivedAt.toLocalDateTime();
      }
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
    return entity != null ? toDomain(entity) : null;
  }

  @Override
  public long countByLocation(String locationIdentifier) {
    return count("location", locationIdentifier);
  }

  @Override
  public List<Warehouse> getAll() {
    return listAll().stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  /**
   * Maps a {@link DbWarehouse} JPA entity to a {@link Warehouse} domain model.
   */
  private Warehouse toDomain(DbWarehouse entity) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = entity.businessUnitCode;
    warehouse.location = entity.location;
    warehouse.capacity = entity.capacity;
    warehouse.stock = entity.stock;
    if (entity.createdAt != null) {
      warehouse.creationAt = entity.createdAt.atZone(ZoneId.systemDefault());
    }
    if (entity.archivedAt != null) {
      warehouse.archivedAt = entity.archivedAt.atZone(ZoneId.systemDefault());
    }
    return warehouse;
  }
}
