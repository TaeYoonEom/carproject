package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_entry_draft")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarEntryDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "car_number", nullable = false)
    private String carNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate; // ✅ year 대신 LocalDate

    private Integer mileage;

    private String region;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_submitted")
    private Boolean isSubmitted;

    @Column(name = "front_view_url")
    private String frontViewUrl;

    @Column(name = "left_side_url")
    private String leftSideUrl;

    @Column(name = "right_side_url")
    private String rightSideUrl;

    @Column(name = "rear_view_url")
    private String rearViewUrl;

    @Column(name = "driver_seat_url")
    private String driverSeatUrl;

    @Column(name = "back_seat_url")
    private String backSeatUrl;

    @Column(name = "exterior_color")
    private String exteriorColor;

    @Column(name = "interior_color")
    private String interiorColor;

    @Column(name = "seat_color")
    private String seatColor;

    @Column(name = "car_id")         // DB 컬럼명: car_id (NULL 허용)
    private Integer carId;

    @Column(name = "drive_type")
    private String driveType;

    @Column(name = "car_type")
    private String carType;

    @Column(name = "fuel_type")
    private String fuelType;

    @Column(name = "transmission")
    private String transmission;

    @Column(name = "is_eco_friendly")
    private Boolean isEcoFriendly;
    // 출고 일정
    @Column(name = "delivery_option")
    private String deliveryOption;

    // 차량 등급 (신차/중고)
    @Column(name = "car_grade")
    private String carGrade;

    // 판매 유형 (위탁/직거래)
    @Column(name = "sale_type")
    private String saleType;

    // 판매 방식 (일반/렌트/리스)
    @Column(name = "sale_method")
    private String saleMethod;




}
