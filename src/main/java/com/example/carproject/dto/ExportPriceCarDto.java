package com.example.carproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExportPriceCarDto {

    private Integer carId;
    private String manufacturer;
    private String modelName; // carName or cargo modelName
    private Integer year;
    private Integer mileage;
    private Integer price;
    private String location; // sale_location or region
}
