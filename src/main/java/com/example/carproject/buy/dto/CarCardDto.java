package com.example.carproject.buy.dto;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.domain.AllCarSale;
import com.example.carproject.domain.CarImage;
import java.util.Optional;
import lombok.*;

@Getter
@Setter
public class CarCardDto {
    private Integer carId;
    private CarSale car;
    private String frontViewUrl;

    public CarCardDto(CarSale car) {
        this.car = car;
        this.carId = car.getCarId(); // ✅ 추가 필수
        this.frontViewUrl = Optional.ofNullable(car.getAllCarSale())
                .flatMap(AllCarSale::getRepresentativeImage)
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

