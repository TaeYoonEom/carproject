// src/main/java/com/example/carproject/domain/CarEntryDraft.java
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

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "car_number", nullable = false)
    private String carNumber;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "manufacturer", length = 50)
    private String manufacturer;   // 제조사

    @Column(name = "model", length = 100)
    private String model;          // 모델(세부명)

    /**
     * 0=국산, 1=수입 (DB: TINYINT(1))
     */
    @Column(name = "origin", nullable = false, columnDefinition = "TINYINT(1)")
    private Integer origin;  // 0=국산, 1=수입

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "mileage")
    private Integer mileage;

    @Builder.Default
    @Column(name = "price", nullable = false)
    private Integer price = 0;

    @Column(name = "region")
    private String region;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_submitted")
    private Boolean isSubmitted;

    // 이미지 URL들
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

    // 색상/구동 등 기초 스펙
    @Column(name = "exterior_color")
    private String exteriorColor;

    @Column(name = "interior_color")
    private String interiorColor;

    @Column(name = "seat_color")
    private String seatColor;

    /**
     * 매핑된 all_car_sale.car_id (NULL 허용)
     */
    @Column(name = "car_id")
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

    @Column(name = "delivery_option")
    private String deliveryOption;

    @Column(name = "car_grade")
    private String carGrade;

    @Column(name = "sale_type")
    private String saleType;

    @Column(name = "sale_method")
    private String saleMethod;

    // ====== 추가된 상태/점검/이력 컬럼들 ======

    /** 타이어 잔량(0~100) (DB: tinyint unsigned) */
    @Column(name = "tire_percentage")
    private Integer tirePercentage;

    /** 엔진오일 이상(0/1) */
    @Column(name = "engine_oil_issue")
    private Boolean engineOilIssue;

    /** 브레이크 이상(0/1) */
    @Column(name = "brake_issue")
    private Boolean brakeIssue;

    /** 성능점검 실시(0/1) */
    @Column(name = "performance_checked")
    private Boolean performanceChecked;

    /** 사고 수리 건수 (tinyint unsigned) */
    @Column(name = "accident_repair_cnt")
    private Integer accidentRepairCnt;

    /** 전손 건수 (tinyint unsigned) */
    @Column(name = "total_loss_cnt")
    private Integer totalLossCnt;

    /** 침수(0/1) */
    @Column(name = "flood_cnt")
    private Integer floodCnt;

    /** 판금 횟수 (tinyint unsigned) */
    @Column(name = "panel_replacement_cnt")
    private Integer panelReplacementCnt;

    /** 보험처리 비용(원) (int) */
    @Column(name = "insurance_claim_cost")
    private Integer insuranceClaimCost;

    /** 타차가해(0/1) */
    @Column(name = "third_party_damage")
    private Boolean thirdPartyDamage;

    /** 특이사항 */
    @Column(name = "special_note")
    private String specialNote;

    /** 판금(0/1) */
    @Column(name = "panel_beating")
    private Boolean panelBeating;

    /** 국소 교환(0/1) */
    @Column(name = "replacement_minor")
    private Boolean replacementMinor;

    /** 부식(0/1) */
    @Column(name = "corrosion")
    private Boolean corrosion;

    // ====== 기본값 처리 ======
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (isSubmitted == null) isSubmitted = Boolean.FALSE;
        if (origin == null) origin = 0;
        if (isEcoFriendly == null) isEcoFriendly = Boolean.FALSE;

        if (accidentRepairCnt == null) accidentRepairCnt = 0;
        if (totalLossCnt == null) totalLossCnt = 0;
        if (floodCnt == null) floodCnt = 0;
        if (panelReplacementCnt == null) panelReplacementCnt = 0;
        if (insuranceClaimCost == null) insuranceClaimCost = 0;

        if (thirdPartyDamage == null) thirdPartyDamage = false;
        if (engineOilIssue == null) engineOilIssue = false;
        if (brakeIssue == null) brakeIssue = false;
        if (performanceChecked == null) performanceChecked = false;

        if (panelBeating == null) panelBeating = false;
        if (replacementMinor == null) replacementMinor = false;
        if (corrosion == null) corrosion = false;
    }
}
