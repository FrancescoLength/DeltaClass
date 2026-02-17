package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArchiveWarehouseUseCaseTest {

    @Mock
    WarehouseStore warehouseStore;

    @InjectMocks
    ArchiveWarehouseUseCase useCase;

    @Test
    void testArchive_Success_SetsArchivedAtAndUpdates() {
        // given
        Warehouse existing = new Warehouse();
        existing.businessUnitCode = "BU-001";
        existing.location = "ZWOLLE-001";
        existing.capacity = 100;
        existing.stock = 50;

        when(warehouseStore.findByBusinessUnitCode("BU-001")).thenReturn(existing);

        Warehouse input = new Warehouse();
        input.businessUnitCode = "BU-001";

        // when
        useCase.archive(input);

        // then - archivedAt is set and update is called
        verify(warehouseStore).update(argThat(w -> {
            assertNotNull(w.archivedAt, "archivedAt should be set");
            return true;
        }));
    }

    @Test
    void testArchive_WarehouseNotFound_NoUpdate() {
        // given
        when(warehouseStore.findByBusinessUnitCode("NONEXISTENT")).thenReturn(null);

        Warehouse input = new Warehouse();
        input.businessUnitCode = "NONEXISTENT";

        // when
        useCase.archive(input);

        // then - only findByBusinessUnitCode was called, no update
        verify(warehouseStore).findByBusinessUnitCode("NONEXISTENT");
        verifyNoMoreInteractions(warehouseStore);
    }
}
