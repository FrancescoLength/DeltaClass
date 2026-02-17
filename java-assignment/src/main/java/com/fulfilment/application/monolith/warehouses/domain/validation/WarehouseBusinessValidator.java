package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

@ApplicationScoped
public class WarehouseBusinessValidator implements WarehouseValidator {

    @Inject
    WarehouseStore warehouseStore;

    @Inject
    LocationResolver locationResolver;

    @Override
    public void validate(Warehouse warehouse, boolean isReplacement) {
        // 1. Business Unit Code Verification
        if (!isReplacement) {
            Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
            if (existing != null) {
                throw new ValidationException("Business Unit Code already exists: " + warehouse.businessUnitCode);
            }
        }

        // 2. Location Validation
        Location location = locationResolver.resolveByIdentifier(warehouse.location);
        if (location == null) {
            throw new ValidationException("Invalid Location: " + warehouse.location);
        }

        // 3. Warehouse Creation Feasibility (Max warehouses per location)
        // If replacement, we assume we take the slot of the old one, so we don't
        // increment.
        // But if location changes during replacement (unlikely but possible?), we
        // should check.
        // Assuming replacement is for the SAME location or we need to check target
        // location.
        // If it's a new warehouse:
        if (!isReplacement) {
            long currentCount = warehouseStore.countByLocation(warehouse.location);
            if (currentCount >= location.maxNumberOfWarehouses) {
                throw new ValidationException(
                        "Maximum number of warehouses reached for location: " + warehouse.location);
            }
        }

        // 4. Capacity and Stock Validation
        if (warehouse.stock > warehouse.capacity) {
            throw new ValidationException(
                    "Stock cannot exceed Capacity. Stock: " + warehouse.stock + ", Capacity: " + warehouse.capacity);
        }

        if (warehouse.capacity > location.maxCapacity) {
            throw new ValidationException("Warehouse capacity exceeds location limit. Capacity: " + warehouse.capacity
                    + ", Max: " + location.maxCapacity);
        }
    }
}
