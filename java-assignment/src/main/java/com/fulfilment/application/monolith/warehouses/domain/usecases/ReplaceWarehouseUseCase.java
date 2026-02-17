package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Use case for replacing an existing warehouse unit.
 * Validates that the existing stock can be accommodated by the replacement's
 * capacity.
 */
@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(ReplaceWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void replace(Warehouse warehouse) {
    LOGGER.infof("Replacing warehouse with business unit code '%s'", warehouse.businessUnitCode);
    warehouseValidator.validate(warehouse, true);
    warehouseStore.update(warehouse);
    LOGGER.infof("Warehouse '%s' replaced successfully", warehouse.businessUnitCode);
  }
}
