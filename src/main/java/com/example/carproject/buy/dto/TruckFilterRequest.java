package com.example.carproject.buy.dto;

import lombok.Data;

import java.util.List;

@Data
public class TruckFilterRequest {
    private List<String> bodyType;
    private List<String> manufacturer;
    private List<String> modelName;
    private List<String> axleConfig;
    private List<String> region;
    private List<String> performanceOpen;
    private List<String> sellerType;
    private List<String> usageType;
    private List<String> color;
    private List<String> fuelType;
    private List<String> transmission;

    private String yearFrom;
    private String yearTo;
    private String monthFrom;
    private String monthTo;
    private Integer mileageMin;
    private Integer mileageMax;
    private Integer priceMin;
    private Integer priceMax;
}

