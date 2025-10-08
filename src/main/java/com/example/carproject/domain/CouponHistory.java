package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "coupon_history")
public class CouponHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "coupon_name", length = 100)
    private String couponName;

    @Column(name = "discount_amount")
    private Integer discountAmount;

    @Column(name = "discount_rate")
    private Float discountRate;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "remaining_days")
    private Integer remainingDays;

    @Column(name = "is_usable")
    private Boolean isUsable;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
