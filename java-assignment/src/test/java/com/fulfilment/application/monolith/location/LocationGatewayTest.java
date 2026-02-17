package com.fulfilment.application.monolith.location;

import org.junit.jupiter.api.Test;

public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    com.fulfilment.application.monolith.warehouses.domain.models.Location location = locationGateway
        .resolveByIdentifier("ZWOLLE-001");

    // then
    org.junit.jupiter.api.Assertions.assertEquals(location.identification, "ZWOLLE-001");
  }
}
