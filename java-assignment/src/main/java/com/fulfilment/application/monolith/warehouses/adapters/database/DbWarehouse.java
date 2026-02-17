package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse {

  @Id
  @GeneratedValue
  public Long id;

  @jakarta.persistence.Column(unique = true)
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;

  public DbWarehouse() {
  }

  public Warehouse toWarehouse() {
    var warehouse = new Warehouse();
    warehouse.setBusinessUnitCode(this.businessUnitCode);
    warehouse.setLocation(this.location);
    warehouse.setCapacity(this.capacity);
    warehouse.setStock(this.stock);
    if (this.createdAt != null) {
      warehouse.setCreationAt(this.createdAt.atZone(java.time.ZoneId.systemDefault()));
    }
    if (this.archivedAt != null) {
      warehouse.setArchivedAt(this.archivedAt.atZone(java.time.ZoneId.systemDefault()));
    }
    return warehouse;
  }
}
