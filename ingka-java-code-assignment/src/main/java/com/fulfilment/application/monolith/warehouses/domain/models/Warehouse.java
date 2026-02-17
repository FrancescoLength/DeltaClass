package com.fulfilment.application.monolith.warehouses.domain.models;

import java.time.ZonedDateTime;

/**
 * Domain model representing a Warehouse Unit.
 * Contains business unit identification, location details, and capacity/stock
 * status.
 */
public class Warehouse {

  // unique identifier
  private String businessUnitCode;

  private String location;

  private Integer capacity;

  private Integer stock;

  private ZonedDateTime creationAt;

  private ZonedDateTime archivedAt;

  public Warehouse() {
  }

  public Warehouse(String businessUnitCode, String location, Integer capacity, Integer stock) {
    this.businessUnitCode = businessUnitCode;
    this.location = location;
    this.capacity = capacity;
    this.stock = stock;
  }

  public String getBusinessUnitCode() {
    return businessUnitCode;
  }

  public void setBusinessUnitCode(String businessUnitCode) {
    this.businessUnitCode = businessUnitCode;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public ZonedDateTime getCreationAt() {
    return creationAt;
  }

  public void setCreationAt(ZonedDateTime creationAt) {
    this.creationAt = creationAt;
  }

  public ZonedDateTime getArchivedAt() {
    return archivedAt;
  }

  public void setArchivedAt(ZonedDateTime archivedAt) {
    this.archivedAt = archivedAt;
  }
}
