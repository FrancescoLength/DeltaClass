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
        Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
        if (!isReplacement) {
            if (existing != null) {
                throw new ValidationException("Business Unit Code already exists: " + warehouse.businessUnitCode);
            }
        } else {
            if (existing == null) {
                throw new ValidationException("Warehouse to replace not found: " + warehouse.businessUnitCode);
            }
            // Additional Validations for Replacing a Warehouse
            // Capacity Accommodation: ensure new capacity can accommodate current stock
            if (warehouse.capacity < existing.stock) {
                throw new ValidationException("New capacity (" + warehouse.capacity
                        + ") cannot be less than current stock (" + existing.stock + ")");
            }
            // Stock Matching: Confirm that the stock of the new warehouse matches the stock
            // of the previous warehouse.
            if (warehouse.stock != existing.stock) {
                throw new ValidationException("Stock of the new warehouse (" + warehouse.stock
                        + ") must match the existing stock (" + existing.stock + ")");
            }
        }

        // 2. Location Validation
        Location location = locationResolver.resolveByIdentifier(warehouse.location);
        if (location == null) {
            throw new ValidationException("Invalid Location: " + warehouse.location);
        }

        // 3. Warehouse Creation Feasibility
        boolean locationChanged = isReplacement && existing != null && !existing.location.equals(warehouse.location);
        if (!isReplacement || locationChanged) {
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
