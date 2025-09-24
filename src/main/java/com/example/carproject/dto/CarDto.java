package com.example.carproject.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CarDto {
    private Long carId;
    private String carName;
    private Integer year;
    private Integer mileage;
    private Integer price;
    private String fuelType;
    private String transmission;
    private String driveType;
    private String exteriorColor;
    private String interiorColor;
    private String saleLocation;
    private String ownershipStatus;
    private String sellerType;
    private String carNumber;
    private String saleType;
    private Integer capacity;
    private LocalDateTime createdAt;

    // ✅ 카테고리 추가
    private String categoryName; // 국산차 / 수입차 / 전기차
    private String categoryPath; // /korean /foreign /ev
}

