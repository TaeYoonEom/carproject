package com.example.carproject.buy.dto;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.domain.CarImage;
import lombok.Getter;

@Getter
public class TruckCardDto {
    private Integer carId;
    private String manufacturer;
    private String modelName;
    private String bodyType;

    private Integer year;
    private Integer month;
    private Integer mileage;
    private Integer price;
    private String region;

    private String frontViewUrl;

    public TruckCardDto(CargoSpecialSale c) {
        this.carId = c.getCarId();
        this.manufacturer = c.getManufacturer();
        this.modelName = c.getModelName();
        this.bodyType = c.getBodyType();
        this.year = c.getYear();
        this.month = c.getMonth();
        this.mileage = c.getMileage();
        this.price = c.getPrice();
        this.region = c.getRegion();

        if (c.getAllCarSale() != null && c.getAllCarSale().getCarImages() != null) {
            this.frontViewUrl = c.getAllCarSale().getCarImages().stream()
                    .filter(img -> img.getIsRepresentative() != null && img.getIsRepresentative())
                    .findFirst()
                    .map(img -> img.getFrontViewUrl())
                    .orElse("/img/default.jpg");
        } else {
            this.frontViewUrl = "/img/default.jpg";
        }
    }
}
