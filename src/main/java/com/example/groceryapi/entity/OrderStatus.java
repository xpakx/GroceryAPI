package com.example.groceryapi.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus 
{
  NEW("Nowe"), SHIPPING("Wysłane"), CANCELED("Anulowane");

  private String status;

  OrderStatus(String status)
  {
    this.status = status;
  }
  
  @JsonValue
  public String getStatus()
  {
    return status;
  }
}
