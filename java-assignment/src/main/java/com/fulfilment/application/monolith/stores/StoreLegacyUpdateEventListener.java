package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class StoreLegacyUpdateEventListener {

    private static final Logger LOGGER = Logger.getLogger(StoreLegacyUpdateEventListener.class);

    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    public void onStoreUpdate(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreLegacyUpdateEvent event) {
        LOGGER.infof("Handling legacy store update for store %s (creation=%s)", event.getStore().name,
                event.isCreation());
        if (event.isCreation()) {
            legacyStoreManagerGateway.createStoreOnLegacySystem(event.getStore());
        } else {
            legacyStoreManagerGateway.updateStoreOnLegacySystem(event.getStore());
        }
    }
}
