package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

/**
 * Use case for archiving an existing warehouse unit.
 * Sets the archivedAt timestamp to soft-delete the warehouse unit.
 */
@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private static final Logger LOGGER = Logger.getLogger(ArchiveWarehouseUseCase.class);

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    LOGGER.infof("Archiving warehouse with business unit code '%s'", warehouse.getBusinessUnitCode());
    Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode());
    if (existing != null) {
      existing.setArchivedAt(java.time.ZonedDateTime.now());
      warehouseStore.update(existing);
      LOGGER.infof("Warehouse '%s' archived successfully", warehouse.getBusinessUnitCode());
    } else {
      LOGGER.warnf("Warehouse '%s' not found for archiving", warehouse.getBusinessUnitCode());
    }
  }
}
