package com.example.carproject.buy.projection;

public interface ElectricCarRow {
    Integer getCarId();
    String  getOrigin();
    String  getCarName();
    Integer getPrice();
    Integer getYear();
    Integer getMileage();
    String  getDriveType();
    String  getSaleLocation();
    String  getOwnershipStatus();
    String  getImageUrl();
}
