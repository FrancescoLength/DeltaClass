package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  private static final Logger LOGGER = Logger.getLogger(LegacyStoreManagerGateway.class);

  public void createStoreOnLegacySystem(Store store) {
    LOGGER.infof("Creating store '%s' on legacy system", store.name);
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    LOGGER.infof("Updating store '%s' on legacy system", store.name);
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    try {
      Path tempFile = Files.createTempFile(store.name, ".txt");
      LOGGER.debugf("Temporary file created at: %s", tempFile);

      String content = "Store created. [ name ="
          + store.name
          + " ] [ items on stock ="
          + store.quantityProductsInStock
          + "]";
      Files.write(tempFile, content.getBytes());
      LOGGER.debugf("Data written to temporary file: %s", content);

      Files.delete(tempFile);
      LOGGER.debug("Temporary file deleted.");
    } catch (Exception e) {
      LOGGER.error("Failed to write store data to temporary file", e);
    }
  }
}
