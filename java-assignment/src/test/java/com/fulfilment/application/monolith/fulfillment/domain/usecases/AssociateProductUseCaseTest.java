package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class AssociateProductUseCaseTest {

    @Mock
    FulfillmentStore fulfillmentStore;

    @Mock
    ProductRepository productRepository;

    @Mock
    StoreRepository storeRepository;

    @Mock
    WarehouseStore warehouseStore;

    AssociateProductUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new AssociateProductUseCase(fulfillmentStore, productRepository, storeRepository, warehouseStore);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAssociate_Success_SavesFulfillment() {
        Fulfillment f = new Fulfillment("Product A", "Store 1", "WH-1");

        // Mock entity existence
        Product product = new Product("Product A");
        PanacheQuery<Product> pQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(productRepository.find(anyString(), anyString())).thenReturn(pQuery);
        when(pQuery.firstResult()).thenReturn(product);

        Store store = new Store("Store 1");
        PanacheQuery<Store> sQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(storeRepository.find(anyString(), anyString())).thenReturn(sQuery);
        when(sQuery.firstResult()).thenReturn(store);

        when(warehouseStore.findByBusinessUnitCode("WH-1")).thenReturn(new Warehouse());

        // No existing association
        when(fulfillmentStore.exists("Product A", "Store 1", "WH-1")).thenReturn(false);

        // All constraints pass
        when(fulfillmentStore.countWarehousesByProductAndStore("Product A", "Store 1")).thenReturn(0L);
        when(fulfillmentStore.countUniqueWarehousesByStore("Store 1")).thenReturn(0L);
        when(fulfillmentStore.countUniqueProductsByWarehouse("WH-1")).thenReturn(0L);

        // when
        useCase.associate(f);

        // then
        verify(fulfillmentStore).save(f);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testAssociate_AlreadyExists_ReturnsWithoutSaving() {
        Fulfillment f = new Fulfillment("Product A", "Store 1", "WH-1");

        // Mock entity existence
        Product product = new Product("Product A");
        PanacheQuery<Product> pQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(productRepository.find(anyString(), anyString())).thenReturn(pQuery);
        when(pQuery.firstResult()).thenReturn(product);

        Store store = new Store("Store 1");
        PanacheQuery<Store> sQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(storeRepository.find(anyString(), anyString())).thenReturn(sQuery);
        when(sQuery.firstResult()).thenReturn(store);

        when(warehouseStore.findByBusinessUnitCode("WH-1")).thenReturn(new Warehouse());

        // Already associated
        when(fulfillmentStore.exists("Product A", "Store 1", "WH-1")).thenReturn(true);

        // when
        useCase.associate(f);

        // then - save is NOT called
        verify(fulfillmentStore, never()).save(any(Fulfillment.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testEnforceMax2WarehousesPerProductPerStore() {
        Fulfillment f = new Fulfillment("Product A", "Store 1", "WH-1");

        // Mock Product existence
        Product product = new Product("Product A");
        PanacheQuery<Product> pQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(productRepository.find(anyString(), anyString())).thenReturn(pQuery);
        when(pQuery.firstResult()).thenReturn(product);

        // Mock Store existence
        Store store = new Store("Store 1");
        PanacheQuery<Store> sQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(storeRepository.find(anyString(), anyString())).thenReturn(sQuery);
        when(sQuery.firstResult()).thenReturn(store);

        // Mock Warehouse existence
        when(warehouseStore.findByBusinessUnitCode("WH-1")).thenReturn(new Warehouse());

        // Constraint violation
        when(fulfillmentStore.countWarehousesByProductAndStore("Product A", "Store 1")).thenReturn(2L);

        assertThrows(ValidationException.class, () -> useCase.associate(f));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testEnforceMax3WarehousesPerStore() {
        Fulfillment f = new Fulfillment("Product A", "Store 1", "WH-3");

        // Mock existence
        Product product = new Product("Product A");
        PanacheQuery<Product> pQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(productRepository.find(anyString(), anyString())).thenReturn(pQuery);
        when(pQuery.firstResult()).thenReturn(product);

        Store store = new Store("Store 1");
        PanacheQuery<Store> sQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(storeRepository.find(anyString(), anyString())).thenReturn(sQuery);
        when(sQuery.firstResult()).thenReturn(store);

        when(warehouseStore.findByBusinessUnitCode("WH-3")).thenReturn(new Warehouse());

        // Constraint violation: Store already has 3 unique warehouses
        when(fulfillmentStore.countUniqueWarehousesByStore("Store 1")).thenReturn(3L);
        // And this warehouse is NOT among them
        when(fulfillmentStore.isWarehouseAssociatedWithStore("WH-3", "Store 1")).thenReturn(false);

        assertThrows(ValidationException.class, () -> useCase.associate(f));
    }

    @Test
    @SuppressWarnings("unchecked")
    void testEnforceMax5ProductsPerWarehouse() {
        Fulfillment f = new Fulfillment("Product New", "Store 1", "WH-1");

        // Mock existence
        Product product = new Product("Product New");
        PanacheQuery<Product> pQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(productRepository.find(anyString(), anyString())).thenReturn(pQuery);
        when(pQuery.firstResult()).thenReturn(product);

        Store store = new Store("Store 1");
        PanacheQuery<Store> sQuery = org.mockito.Mockito.mock(PanacheQuery.class);
        when(storeRepository.find(anyString(), anyString())).thenReturn(sQuery);
        when(sQuery.firstResult()).thenReturn(store);

        when(warehouseStore.findByBusinessUnitCode("WH-1")).thenReturn(new Warehouse());

        // Constraint violation: Warehouse already has 5 types of products
        when(fulfillmentStore.countUniqueProductsByWarehouse("WH-1")).thenReturn(5L);
        // And this product is NOT among them
        when(fulfillmentStore.isProductAssociatedWithWarehouse("Product New", "WH-1")).thenReturn(false);

        assertThrows(ValidationException.class, () -> useCase.associate(f));
    }
}
