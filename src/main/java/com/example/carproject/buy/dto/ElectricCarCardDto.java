package com.example.carproject.buy.dto;

import lombok.*;

@Getter @AllArgsConstructor
public class ElectricCarCardDto {
    private final Integer carId;
    private final String  origin;
    private final String  carName;
    private final Integer price;
    private final Integer year;
    private final Integer mileage;
    private final String  driveType;
    private final String  saleLocation;
    private final String  ownershipStatus;
    private final String  imageUrl;
}
