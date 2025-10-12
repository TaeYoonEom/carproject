// src/main/java/.../domain/CarConditionHistory.java
package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_condition_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CarConditionHistory {

    @Id
    @Column(name = "car_id")
    private Integer carId;                      // all_car_sale.car_id (초기엔 draft id를 넘겨도 OK)

    @Column(name = "tire_percentage")
    private Integer tirePercentage;             // 0~100

    @Column(name = "engine_oil_issue")
    private Boolean engineOilIssue;             // 0/1

    @Column(name = "brake_issue")
    private Boolean brakeIssue;

    @Column(name = "performance_checked")
    private Boolean performanceChecked;         // 성능점검 실시여부

    @Column(name = "accident_repair_cnt")
    private Integer accidentRepairCnt;

    @Column(name = "total_loss_cnt")
    private Integer totalLossCnt;

    @Column(name = "flood_cnt")
    private Integer floodCnt;

    @Column(name = "panel_replacement_cnt")
    private Integer panelReplacementCnt;

    @Column(name = "insurance_claim_cost")
    private Integer insuranceClaimCost;         // 원

    @Column(name = "third_party_damage")
    private Boolean thirdPartyDamage;

    @Column(name = "special_note", length = 255)
    private String specialNote;

    @Column(name = "panel_beating")
    private Boolean panelBeating;

    @Column(name = "replacement_minor")
    private Boolean replacementMinor;

    @Column(name = "corrosion")
    private Boolean corrosion;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
