package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

/**
 * Validator for warehouse business rules.
 * Enforces constraints such as:
 * - Uniqueness of Business Unit Code (BUC) per location.
 * - Maximum number of warehouses per location.
 * - Minimum capacity requirements for existing stock during replacement.
 */
@ApplicationScoped
public class WarehouseBusinessValidator implements WarehouseValidator {

    @Inject
    WarehouseStore warehouseStore;

    @Inject
    LocationResolver locationResolver;

    @Override
    public void validate(Warehouse warehouse, boolean isReplacement) {
        // 1. Business Unit Code Verification
        Warehouse existing = warehouseStore.findByBusinessUnitCode(warehouse.getBusinessUnitCode());
        if (!isReplacement) {
            if (existing != null) {
                throw new ValidationException("Business Unit Code already exists: " + warehouse.getBusinessUnitCode());
            }
        } else {
            if (existing == null) {
                throw new ValidationException("Warehouse to replace not found: " + warehouse.getBusinessUnitCode());
            }
            // Additional Validations for Replacing a Warehouse
            // Capacity Accommodation: ensure new capacity can accommodate current stock
            if (warehouse.getCapacity() < existing.getStock()) {
                throw new ValidationException("New capacity (" + warehouse.getCapacity()
                        + ") cannot be less than current stock (" + existing.getStock() + ")");
            }
            // Stock Matching: Confirm that the stock of the new warehouse matches the stock
            // of the previous warehouse.
            if (!warehouse.getStock().equals(existing.getStock())) {
                throw new ValidationException("Stock of the new warehouse (" + warehouse.getStock()
                        + ") must match the existing stock (" + existing.getStock() + ")");
            }
        }

        // 2. Location Validation
        Location location = locationResolver.resolveByIdentifier(warehouse.getLocation());
        if (location == null) {
            throw new ValidationException("Invalid Location: " + warehouse.getLocation());
        }

        // 3. Warehouse Creation Feasibility
        boolean locationChanged = isReplacement && existing != null
                && !existing.getLocation().equals(warehouse.getLocation());
        if (!isReplacement || locationChanged) {
            long currentCount = warehouseStore.countByLocation(warehouse.getLocation());
            if (currentCount >= location.maxNumberOfWarehouses) {
                throw new ValidationException(
                        "Maximum number of warehouses reached for location: " + warehouse.getLocation());
            }
        }

        // 4. Capacity and Stock Validation
        if (warehouse.getStock() > warehouse.getCapacity()) {
            throw new ValidationException(
                    "Stock cannot exceed Capacity. Stock: " + warehouse.getStock() + ", Capacity: "
                            + warehouse.getCapacity());
        }

        if (warehouse.getCapacity() > location.maxCapacity) {
            throw new ValidationException(
                    "Warehouse capacity exceeds location limit. Capacity: " + warehouse.getCapacity()
                            + ", Max: " + location.maxCapacity);
        }
    }
}
