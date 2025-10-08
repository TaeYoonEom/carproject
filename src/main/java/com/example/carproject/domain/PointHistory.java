package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "point_history")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "type", columnDefinition = "ENUM('적립','사용','만료')")
    private String type;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "earned_points")
    private Integer earnedPoints;

    @Column(name = "used_points")
    private Integer usedPoints;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
