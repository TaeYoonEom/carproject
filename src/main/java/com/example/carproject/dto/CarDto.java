package com.example.carproject.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    private String categoryName;
    private String categoryPath;

    // 🔥 화물차 여부 (정답)
    private boolean cargo;

    // 🔥 화물차 전용 필드
    private String manufacturer;
    private String modelName;
    private String bodyType;
    private String axleConfig;
    private String usageType;
    private BigDecimal loadCapacityTon;
    private String cargoColor;
    private Integer month;              // 제조월
    private String encarDiagnosis;      // 엔카진단 / 미진단
    private String performanceOpen;     // 성능/보험/이력 공개
    private List<String> cargoOptions;  // 옵션 목록(,로 split)

}



