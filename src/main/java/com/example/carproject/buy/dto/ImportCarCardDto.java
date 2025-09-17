package com.example.carproject.buy.dto;

import com.example.carproject.importcar.domain.ImportCarSale;
import com.example.carproject.domain.CarImage;
import lombok.Getter;

@Getter
public class ImportCarCardDto {
    private final ImportCarSale car;   // 엔티티를 품은 DTO
    private final String frontViewUrl;

    public ImportCarCardDto(ImportCarSale car) {
        this.car = car;
        // allCarSale or carImages가 없을 수도 있으니 안전하게
        this.frontViewUrl = car.getAllCarSale()
                .getRepresentativeImage()
                .map(CarImage::getFrontViewUrl)
                .orElse("/img/default.jpg");
    }

    // 템플릿 편의를 위한 위임 getter
    public Integer getCarId() { return car.getCarId(); }
    public String  getCarName() { return car.getCarName(); }
    public Integer getYear() { return car.getYear(); }
    public Integer getMileage() { return car.getMileage(); }
    public String  getSaleLocation() { return car.getSaleLocation(); }
    public Integer getPrice() { return car.getPrice(); }
    public String  getDriveType() { return car.getDriveType(); }
    public String  getOwnershipStatus() { return car.getOwnershipStatus(); }
}
