package com.example.carproject.buy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.time.LocalDateTime;

@Getter
@Entity
@Immutable
@Table(name = "eco_car_flat")
public class EcoCar {

    @Id
    @Column(name = "car_id")
    private Integer carId;

    private Integer origin;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "car_name")
    private String carName;

    private String manufacturer;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "car_number")
    private String carNumber;


    @Column(name = "ownership_status")
    private String ownershipStatus;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "delivery_option")
    private String deliveryOption;

    @Column(name = "exterior_color")
    private String exteriorColor;

    @Column(name = "interior_color")
    private String interiorColor;

    @Column(name = "seat_color")
    private String seatColor;

    @Column(name = "drive_type")
    private String driveType;

    @Column(name = "sale_location")
    private String saleLocation;

    private Integer price;

    private Integer mileage;

    private Integer year;

    private Integer month;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "car_type")
    private String carType;

    @Column(name = "car_grade")
    private String carGrade;

    private Integer capacity;

    @Column(name = "sale_type")
    private String saleType;

    @Column(name = "fuel_type")
    private String fuelType;

    private String transmission;

    @Column(name = "performance_open")
    private String performanceOpen;

    @Column(name = "seller_type")
    private String sellerType;

    @Column(name = "sale_method")
    private String saleMethod;

    @Column(name = "image_url")
    private String imageUrl;
}

