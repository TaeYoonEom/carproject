package com.example.carproject.buy.domain;

import com.example.carproject.domain.AllCarSale;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class CarSale {

    @Id
    @Column(name = "car_id")
    private Integer carId; // ✅ GeneratedValue 제거 (공유 PK)
    private int memberId;
    private String carName;
    private String manufacturer;
    private String modelName;
    private String carNumber;

    @Column(columnDefinition = "ENUM('소유중', '판매완료', '등록중')")
    private String ownershipStatus;
    private String ownerName;
    private String deliveryOption;
    private String exteriorColor;
    private String interiorColor;
    private String seatColor;
    private String driveType;
    private String saleLocation;
    private int price;
    private int mileage;
    private int year;
    private int month;
    private LocalDateTime createdAt;

    @Column(length = 50)
    private String carType;
    private String carGrade;
    private int capacity; //인승
    private String saleType;
    private String fuelType;

    @Column(length = 50)
    private String transmission; //변속기

    @Column(columnDefinition = "ENUM('엔카 직영 성능점검', '성능기록부', '보험이력', '차량 이력 공개')")
    private String performanceOpen;

    @Column(columnDefinition = "ENUM('개인', '딜러', '리스렌트제휴')")
    private String sellerType;

    @Column(columnDefinition = "ENUM('일반', '렌트', '리스')")
    private String saleMethod;

    // AllCarSale 연결 추가
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "all_car_sale_id", referencedColumnName = "car_id")
    private AllCarSale allCarSale;

}