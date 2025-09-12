package com.example.carproject.dto;

import lombok.Data;

@Data
public class ComparableCarDto {
    private String carName;
    private Integer year;
    private Integer mileage;
    private Integer price;
    private String saleLocation;
}
