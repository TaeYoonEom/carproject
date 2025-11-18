package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_purchase_recent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarPurchaseRecent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;
}
