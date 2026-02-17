package com.fulfilment.application.monolith.stores;

public class StoreLegacyUpdateEvent {

    private final Store store;
    private final boolean isCreation;

    public StoreLegacyUpdateEvent(Store store, boolean isCreation) {
        this.store = store;
        this.isCreation = isCreation;
    }

    public Store getStore() {
        return store;
    }

    public boolean isCreation() {
        return isCreation;
    }
}
