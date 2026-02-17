package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class WarehouseBusinessValidatorTest {

    @Mock
    WarehouseStore warehouseStore;

    @Mock
    LocationResolver locationResolver;

    @InjectMocks
    WarehouseBusinessValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateReplacementCapacitySuccess() {
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "WH-1";
        existing.stock = 50;
        existing.capacity = 100;
        existing.location = "LOC-1";

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "WH-1";
        replacement.capacity = 60; // > 50
        replacement.stock = 50;
        replacement.location = "LOC-1";

        Location location = new Location("LOC-1", 5, 200);

        when(warehouseStore.findByBusinessUnitCode("WH-1")).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(location);

        assertDoesNotThrow(() -> validator.validate(replacement, true));
    }

    @Test
    void testValidateReplacementCapacityFailure() {
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "WH-1";
        existing.stock = 80;
        existing.capacity = 100;
        existing.location = "LOC-1";

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "WH-1";
        replacement.capacity = 50; // < 80
        replacement.stock = 50;
        replacement.location = "LOC-1";

        Location location = new Location("LOC-1", 5, 200);

        when(warehouseStore.findByBusinessUnitCode("WH-1")).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(location);

        assertThrows(ValidationException.class, () -> validator.validate(replacement, true));
    }

    @Test
    void testValidateReplacementStockFailure() {
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "WH-1";
        existing.stock = 80;
        existing.capacity = 100;
        existing.location = "LOC-1";

        Warehouse replacement = new Warehouse();
        replacement.businessUnitCode = "WH-1";
        replacement.capacity = 100;
        replacement.stock = 50; // < 80, mismatch
        replacement.location = "LOC-1";

        Location location = new Location("LOC-1", 5, 200);

        when(warehouseStore.findByBusinessUnitCode("WH-1")).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(location);

        assertThrows(ValidationException.class, () -> validator.validate(replacement, true));
    }

    @Test
    void testValidateMaxWarehousesPerLocationFailure() {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "WH-NEW";
        warehouse.location = "LOC-1";
        warehouse.capacity = 100;
        warehouse.stock = 0;

        Location location = new Location("LOC-1", 2, 500);

        when(warehouseStore.findByBusinessUnitCode("WH-NEW")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(location);
        when(warehouseStore.countByLocation("LOC-1")).thenReturn(2L);

        assertThrows(ValidationException.class, () -> validator.validate(warehouse, false));
    }
}
