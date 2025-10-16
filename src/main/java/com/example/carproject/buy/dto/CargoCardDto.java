package com.example.carproject.buy.dto;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.domain.CarImage;
import lombok.Getter;

@Getter
public class CargoCardDto {
    private final CargoSpecialSale car;
    private final String frontViewUrl;

    public CargoCardDto(CargoSpecialSale car) {
        this.car = car;
        // 국산차와 동일한 접근 통일: AllCarSale → 대표 이미지 Optional
        this.frontViewUrl = car.getAllCarSale() != null
                ? car.getAllCarSale().getRepresentativeImage()
                .map(CarImage::getFrontViewUrl)
                .orElse("/img/default.jpg")
                : "/img/default.jpg";
    }

    // 템플릿에서 바로 쓰기 좋은 게터들
    public Integer getCarId()      { return car.getCarId(); }
    public String  getManufacturer(){ return car.getManufacturer(); }
    public String  getModelName()  { return car.getModelName(); }
    public Integer getYear()       { return car.getYear(); }
    public Integer getMonth()      { return car.getMonth(); }
    public Integer getMileage()    { return car.getMileage(); }
    public String  getBodyType()   { return car.getBodyType(); }
    public String  getRegion()     { return car.getRegion(); }
    public Integer getPrice()      { return car.getPrice(); }
    public String  getOption()     { return car.getOptions(); } // 엔티티 'options' 매핑
}
