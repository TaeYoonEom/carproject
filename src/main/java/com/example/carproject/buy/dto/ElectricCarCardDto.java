package com.example.carproject.buy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ElectricCarCardDto {
    private final Integer carId;
    private final Integer  origin;
    private final String  carName;
    private final Integer price;
    private final Integer year;
    private final Integer month;
    private final Integer mileage;
    private final String  driveType;
    private final String  saleLocation;
    private final String  ownershipStatus;
    private final String  imageUrl;

    private final String  manufacturer;
    private final String  modelName;
    private final String  fuelType;
    private final String  transmission;
    private final Integer capacity;
    private final String  carType;

    private final String  performanceOpen;
    private final String  sellerType;
    private final String  saleMethod;


}
