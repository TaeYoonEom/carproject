package com.example.carproject.buy.dto;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.domain.ImportCarSale;
import com.example.carproject.domain.CarImage;
import lombok.Getter;
@Getter
public class ImportCarCardDto {

    // 필수 정보 (카드에 거의 항상 쓰임)
    private final Integer carId;
    private final String carName;
    private final Integer year;
    private final Integer mileage;
    private final String saleLocation;
    private final Integer price;
    private final String driveType;
    private final String ownershipStatus;
    private final String frontViewUrl;

    // 확장 필드 (필터/상세/미래 UI 고려해서 포함)
    private final String manufacturer;
    private final String modelName;
    private final String exteriorColor;
    private final String interiorColor;
    private final String saleType;
    private final String sellerType;
    private final String saleMethod;
    private final String fuelType;
    private final Integer capacity;

    public ImportCarCardDto(ImportCarSale car) {
        this.carId = car.getCarId();
        this.carName = car.getCarName();
        this.year = car.getYear();
        this.mileage = car.getMileage();
        this.saleLocation = car.getSaleLocation();
        this.price = car.getPrice();
        this.driveType = car.getDriveType();
        this.ownershipStatus = car.getOwnershipStatus();

        this.manufacturer = car.getManufacturer();
        this.modelName = car.getModelName();
        this.exteriorColor = car.getExteriorColor();
        this.interiorColor = car.getInteriorColor();
        this.saleType = car.getSaleType();
        this.sellerType = car.getSellerType();
        this.saleMethod = car.getSaleMethod();
        this.fuelType = car.getFuelType();
        this.capacity = car.getCapacity();

        this.frontViewUrl = car.getAllCarSale()
                .getRepresentativeImage()
                .map(CarImage::getFrontViewUrl)
                .orElse("/img/default.jpg");
    }
}
