package com.example.carproject.buy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CargoCardDto {
    private final Integer carId;
    private final String title;      // manufacturer + modelName
    private final Integer year;
    private final Integer month;
    private final Integer mileage;
    private final String bodyType;   // 차체형식(또는 축정보)
    private final String region;     // 지역
    private final Integer price;
    private final String imageUrl;   // 대표 이미지(없으면 대체)
    private final String option;
}