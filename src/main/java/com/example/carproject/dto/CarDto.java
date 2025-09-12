package com.example.carproject.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CarDto {
    private Long carId;
    private String carName;
    private Integer year;
    private Integer mileage;
    private Integer price;          // 원 단위
    private String fuelType;
    private String transmission;
    private String driveType;
    private String exteriorColor;
    private String interiorColor;
    private String saleLocation;
    private String ownershipStatus; // 소유중/판매완료 등
    private String sellerType;      // 개인/딜러
    private String carNumber;
    private String saleType;        // 일반/리스/렌트 (옵션)
    private Integer capacity;       // 배기량(cc) (옵션)
    private LocalDateTime createdAt;
}
