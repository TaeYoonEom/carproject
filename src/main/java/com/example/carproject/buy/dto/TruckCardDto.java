package com.example.carproject.buy.dto;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.domain.CarImage;
import lombok.Getter;

@Getter
public class TruckCardDto {
    private final CargoSpecialSale car;
    private final String frontViewUrl;

    public TruckCardDto(CargoSpecialSale car) {
        this.car = car;
        this.frontViewUrl = car.getAllCarSale() != null
                ? car.getAllCarSale().getRepresentativeImage()
                .map(CarImage::getFrontViewUrl)
                .orElse("/img/default.jpg")
                : "/img/default.jpg";
    }

    // ✅ Thymeleaf에서 바로 쓰는 getter들
    public Integer getCarId()      { return car.getCarId(); }
    public String  getManufacturer(){ return car.getManufacturer(); }
    public String  getModelName()  { return car.getModelName(); }
    public Integer getYear()       { return car.getYear(); }
    public Integer getMonth()      { return car.getMonth(); }
    public Integer getMileage()    { return car.getMileage(); }
    public String  getBodyType()   { return car.getBodyType(); }
    public String  getRegion()     { return car.getRegion(); }
    public Integer getPrice()      { return car.getPrice(); }
    public String  getOption()     { return car.getOptions(); }
}
