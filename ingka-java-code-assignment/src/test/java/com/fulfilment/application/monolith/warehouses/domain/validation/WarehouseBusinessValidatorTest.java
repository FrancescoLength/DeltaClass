package com.fulfilment.application.monolith.warehouses.domain.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WarehouseBusinessValidatorTest {

    @Mock
    WarehouseStore warehouseStore;

    @Mock
    LocationResolver locationResolver;

    @InjectMocks
    WarehouseBusinessValidator validator;

    private Warehouse warehouse;
    private Location location;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setBusinessUnitCode("BU001");
        warehouse.setLocation("LOC1");
        warehouse.setCapacity(100);
        warehouse.setStock(50);

        location = new Location("LOC1", 5, 200);
    }

    @Test
    void testValidate_Success() {
        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);
        when(warehouseStore.countByLocation("LOC1")).thenReturn(2L);

        assertDoesNotThrow(() -> validator.validate(warehouse, false));
    }

    @Test
    void testValidate_AlreadyExists() {
        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(new Warehouse());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(warehouse, false));
        assertEquals("Business Unit Code already exists: BU001", exception.getMessage());
    }

    @Test
    void testValidate_InvalidLocation() {
        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(warehouse, false));
        assertEquals("Invalid Location: LOC1", exception.getMessage());
    }

    @Test
    void testValidate_MaxWarehouses() {
        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);
        when(warehouseStore.countByLocation("LOC1")).thenReturn(5L);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(warehouse, false));
        assertEquals("Maximum number of warehouses reached for location: LOC1", exception.getMessage());
    }

    @Test
    void testValidate_StockExceedsCapacity() {
        warehouse.setStock(150);
        warehouse.setCapacity(100);
        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(warehouse, false));
        assertEquals("Stock cannot exceed Capacity. Stock: 150, Capacity: 100", exception.getMessage());
    }

    @Test
    void testValidate_CapacityExceedsLocationLimit() {
        warehouse.setCapacity(300);
        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(null);
        when(locationResolver.resolveByIdentifier("LOC1")).thenReturn(location);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(warehouse, false));
        assertEquals("Warehouse capacity exceeds location limit. Capacity: 300, Max: 200", exception.getMessage());
    }

    @Test
    void testValidate_Replacement_CapacityAccommodationFailure() {
        Warehouse existing = new Warehouse();
        existing.setBusinessUnitCode("BU001");
        existing.setStock(120); // more than new capacity 100

        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(existing);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(warehouse, true));
        assertEquals("New capacity (100) cannot be less than current stock (120)", exception.getMessage());
    }

    @Test
    void testValidate_Replacement_StockMatchingFailure() {
        Warehouse existing = new Warehouse();
        existing.setBusinessUnitCode("BU001");
        existing.setStock(50);

        warehouse.setStock(60); // Doesn't match
        warehouse.setCapacity(100);

        when(warehouseStore.findByBusinessUnitCode("BU001")).thenReturn(existing);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> validator.validate(warehouse, true));
        assertEquals("Stock of the new warehouse (60) must match the existing stock (50)", exception.getMessage());
    }
}
