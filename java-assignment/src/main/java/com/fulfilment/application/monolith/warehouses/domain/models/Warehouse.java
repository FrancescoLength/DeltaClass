package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.LocalDateTime;

/**
 * Domain model representing a Warehouse Unit.
 * Contains business unit identification, location details, and capacity/stock
 * status.
 */
public class Warehouse {

  // unique identifier
  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;
}
