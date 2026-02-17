package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * JAX-RS implementation for the Warehouse resource.
 * Exposes endpoints for managing the warehouse lifecycle (Create, Read, Update,
 * Archive).
 */
@jakarta.enterprise.context.ApplicationScoped
public class WarehouseResourceImpl implements WarehouseResource {

  private final com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore warehouseStore;
  private final com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation createWarehouseOperation;
  private final com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation archiveWarehouseOperation;
  private final com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation replaceWarehouseOperation;

  @jakarta.inject.Inject
  public WarehouseResourceImpl(
      com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore warehouseStore,
      com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation createWarehouseOperation,
      com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation archiveWarehouseOperation,
      com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation replaceWarehouseOperation) {
    this.warehouseStore = warehouseStore;
    this.createWarehouseOperation = createWarehouseOperation;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
    this.replaceWarehouseOperation = replaceWarehouseOperation;
  }

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseStore.getAll().stream()
        .map(this::toApi)
        .collect(java.util.stream.Collectors.toList());
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse = fromApi(data);
    createWarehouseOperation.create(domainWarehouse);
    return data;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse = warehouseStore
        .findByBusinessUnitCode(id);
    if (domainWarehouse == null) {
      throw new jakarta.ws.rs.WebApplicationException("Warehouse not found", 404);
    }
    return toApi(domainWarehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domainWarehouse = warehouseStore
        .findByBusinessUnitCode(id);
    if (domainWarehouse == null) {
      throw new jakarta.ws.rs.WebApplicationException("Warehouse not found", 404);
    }
    archiveWarehouseOperation.archive(domainWarehouse);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode, @NotNull Warehouse data) {
    data.setBusinessUnitCode(businessUnitCode);
    replaceWarehouseOperation.replace(fromApi(data));
    return data;
  }

  private Warehouse toApi(com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domain) {
    Warehouse api = new Warehouse();
    api.setId(domain.businessUnitCode);
    api.setBusinessUnitCode(domain.businessUnitCode);
    api.setLocation(domain.location);
    api.setCapacity(domain.capacity);
    api.setStock(domain.stock);
    return api;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse fromApi(Warehouse api) {
    com.fulfilment.application.monolith.warehouses.domain.models.Warehouse domain = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.businessUnitCode = api.getBusinessUnitCode() != null ? api.getBusinessUnitCode() : api.getId();
    domain.location = api.getLocation();
    domain.capacity = api.getCapacity();
    domain.stock = api.getStock();
    return domain;
  }
}
