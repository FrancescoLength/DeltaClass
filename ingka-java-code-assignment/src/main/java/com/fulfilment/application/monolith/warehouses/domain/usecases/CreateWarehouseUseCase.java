package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Use case for creating a new warehouse unit.
 * Coordinates validation and persistence of the new warehouse.
 */
@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(CreateWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void create(Warehouse warehouse) {
    LOGGER.infof("Creating warehouse with business unit code '%s' at location '%s'",
        warehouse.getBusinessUnitCode(), warehouse.getLocation());
    warehouseValidator.validate(warehouse, false);
    warehouseStore.create(warehouse);
    LOGGER.infof("Warehouse '%s' created successfully", warehouse.getBusinessUnitCode());
  }
}
