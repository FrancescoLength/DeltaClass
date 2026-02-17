package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseValidator;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateWarehouseUseCaseTest {

    @Mock
    WarehouseStore warehouseStore;

    @Mock
    WarehouseValidator warehouseValidator;

    @InjectMocks
    CreateWarehouseUseCase useCase;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU-NEW";
        warehouse.location = "ZWOLLE-001";
        warehouse.capacity = 100;
        warehouse.stock = 50;
    }

    @Test
    void testCreate_Success_ValidatesAndPersists() {
        // when
        useCase.create(warehouse);

        // then - validator called with isReplacement=false, then store persists
        verify(warehouseValidator).validate(warehouse, false);
        verify(warehouseStore).create(warehouse);
    }

    @Test
    void testCreate_ValidationFails_DoesNotPersist() {
        // given
        doThrow(new ValidationException("Business Unit Code already exists: BU-NEW"))
                .when(warehouseValidator).validate(warehouse, false);

        // when / then
        assertThrows(ValidationException.class, () -> useCase.create(warehouse));
        verifyNoInteractions(warehouseStore);
    }
}
