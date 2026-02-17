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
public class ReplaceWarehouseUseCaseTest {

    @Mock
    WarehouseStore warehouseStore;

    @Mock
    WarehouseValidator warehouseValidator;

    @InjectMocks
    ReplaceWarehouseUseCase useCase;

    private Warehouse newWarehouse;

    @BeforeEach
    void setUp() {
        newWarehouse = new Warehouse();
        newWarehouse.setBusinessUnitCode("BU-EXISTING");
        newWarehouse.setLocation("ZWOLLE-001");
        newWarehouse.setCapacity(120);
        newWarehouse.setStock(50);
    }

    @Test
    void testReplace_Success_ValidatesAndUpdates() {
        // when
        useCase.replace(newWarehouse);

        // then - validator called with isReplacement=true, then store updates
        verify(warehouseValidator).validate(newWarehouse, true);
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void testReplace_ValidationFails_DoesNotUpdate() {
        // given
        doThrow(new ValidationException("Stock of the new warehouse (60) must match the existing stock (50)"))
                .when(warehouseValidator).validate(newWarehouse, true);

        // when / then
        assertThrows(ValidationException.class, () -> useCase.replace(newWarehouse));
        verifyNoInteractions(warehouseStore);
    }
}
