package com.example.carproject.buy.dto;

import com.example.carproject.buy.projection.ElectricCarRow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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

    public static ElectricCarCardDto from(ElectricCarRow r) {
        return new ElectricCarCardDto(
                r.getCarId(),
                r.getOrigin(),
                r.getCarName(),
                r.getPrice(),
                r.getYear(),
                r.getMileage(),
                r.getDriveType(),
                r.getSaleLocation(),
                r.getOwnershipStatus(),
                r.getImageUrl()
        );
    }
}
