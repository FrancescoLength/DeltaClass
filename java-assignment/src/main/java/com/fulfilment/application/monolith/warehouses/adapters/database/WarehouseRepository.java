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
    entity.businessUnitCode = warehouse.getBusinessUnitCode();
    entity.location = warehouse.getLocation();
    entity.capacity = warehouse.getCapacity();
    entity.stock = warehouse.getStock();
    entity.createdAt = LocalDateTime.now();
    this.persist(entity);
  }

  @Override
  @jakarta.transaction.Transactional
  public void update(Warehouse warehouse) {
    DbWarehouse entity = find("businessUnitCode", warehouse.getBusinessUnitCode()).firstResult();
    if (entity != null) {
      entity.location = warehouse.getLocation();
      entity.capacity = warehouse.getCapacity();
      entity.stock = warehouse.getStock();
      if (warehouse.getArchivedAt() != null) {
        entity.archivedAt = warehouse.getArchivedAt().toLocalDateTime();
      }
    }
  }

  @Override
  @jakarta.transaction.Transactional
  public void remove(Warehouse warehouse) {
    delete("businessUnitCode", warehouse.getBusinessUnitCode());
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
    warehouse.setBusinessUnitCode(entity.businessUnitCode);
    warehouse.setLocation(entity.location);
    warehouse.setCapacity(entity.capacity);
    warehouse.setStock(entity.stock);
    if (entity.createdAt != null) {
      warehouse.setCreationAt(entity.createdAt.atZone(ZoneId.systemDefault()));
    }
    if (entity.archivedAt != null) {
      warehouse.setArchivedAt(entity.archivedAt.atZone(ZoneId.systemDefault()));
    }
    return warehouse;
  }
}
