package com.example.carproject.importcar.domain;

import com.example.carproject.domain.AllCarSale;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ImportCarSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int carId;

    private int memberId;
    private String carName;
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
    private LocalDateTime createdAt;

    private String carGrade;
    private int capacity;
    private String saleType;
    private String fuelType;

    @Column(length = 50)
    private String transmission;

    @Column(length = 50)
    private String carType;  // 수입

    @OneToOne
    @JoinColumn(name = "all_car_sale_id")   // FK → all_car_sale.car_id
    private AllCarSale allCarSale;
}
