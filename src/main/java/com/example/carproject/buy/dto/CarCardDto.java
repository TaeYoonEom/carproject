package com.example.carproject.buy.dto;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.domain.CarImage;
import lombok.Getter;

@Getter
public class CarCardDto {
    private final CarSale car;
    private final String frontViewUrl;

    public CarCardDto(CarSale car) {
        this.car = car;
        this.frontViewUrl = car.getAllCarSale()
                .getRepresentativeImage()
                .map(CarImage::getFrontViewUrl)
                .orElse("/img/default.jpg");
    }
    // CarCardDto.java
    public String getCarName() { return car.getCarName(); }
    public int getYear() { return car.getYear(); }
    public int getMileage() { return car.getMileage(); }
    public String getSaleLocation() { return car.getSaleLocation(); }
    public int getPrice() { return car.getPrice(); }
    public String getOwnershipStatus() { return car.getOwnershipStatus(); }
    public String getDriveType() { return car.getDriveType(); }


}

