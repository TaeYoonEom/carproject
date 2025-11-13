package com.example.carproject.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "inquiry")
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "car_id", nullable = false)
    private Integer carId;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // ⭐ all_car_sale 매핑 (조회 전용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", insertable = false, updatable = false)
    private AllCarSale car;
}
