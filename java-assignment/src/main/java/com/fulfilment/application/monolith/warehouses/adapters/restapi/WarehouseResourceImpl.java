package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * JAX-RS implementation for the Warehouse resource.
 * Exposes endpoints for managing the warehouse lifecycle (Create, Read, Update,
 * Archive).
 */
@ApplicationScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  private WarehouseRepository warehouseRepository;
  @Inject
  private com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation createWarehouseOperation;
  @Inject
  private com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation replaceWarehouseOperation;
  @Inject
  private com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation archiveWarehouseOperation;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    createWarehouseOperation.create(toDomain(data));
    return data;
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    var warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse == null) {
      // Return 404 or null depending on framework handling.
      // Genereted interface might handle null as 204 or throw
      // WebApplicationException.
      // Let's return null and see or throw exception if I can verify behavior.
      // StoreResource threw WebApplicationException(404).
      throw new jakarta.ws.rs.WebApplicationException("Warehouse not found", 404);
    }
    return toWarehouseResponse(warehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    // We only have ID. Creating a partial warehouse object or finding it first?
    // ArchiveWarehouseUseCase uses findByBusinessUnitCode inside.
    // So checking if it expects a full object.
    // ArchiveWarehouseUseCase: findByBusinessUnitCode(warehouse.businessUnitCode).
    var warehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    warehouse.businessUnitCode = id;
    archiveWarehouseOperation.archive(warehouse);
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    data.setBusinessUnitCode(businessUnitCode); // Ensure ID matches path
    replaceWarehouseOperation.replace(toDomain(data));
    return data;
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);
    return response;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomain(Warehouse dto) {
    var domain = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    domain.businessUnitCode = dto.getBusinessUnitCode();
    domain.location = dto.getLocation();
    domain.capacity = dto.getCapacity();
    domain.stock = dto.getStock();
    return domain;
  }
}
