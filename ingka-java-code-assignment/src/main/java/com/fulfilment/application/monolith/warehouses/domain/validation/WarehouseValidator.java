package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public interface WarehouseValidator {
    void validate(Warehouse warehouse, boolean isReplacement);
}
