package com.example.carproject.buy.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImportFilterRequest {
    private List<String> carType;
    private List<String> manufacturer;
    private List<String> modelName;
    private List<String> carName;
    private List<String> fuelType;
    private List<String> transmission;
    private List<String> saleLocation;
    private List<String> sellerType;
    private List<String> saleMethod;
    private List<String> exteriorColor;
    private List<String> interiorColor;
    private List<String> performanceOpen;
    private List<String> capacity;

    // 숫자 범위
    private Integer priceMin;
    private Integer priceMax;
    private Integer yearFrom;
    private Integer yearTo;
    private Integer monthFrom;
    private Integer monthTo;
    private Integer mileageMin;
    private Integer mileageMax;
}