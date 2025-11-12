package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_sold",
        uniqueConstraints = @UniqueConstraint(name = "uq_car_sold_car", columnNames = "car_id"))
@Getter
@Setter
public class CarSold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;   // ✅ int(11)

    @Column(name = "member_id", nullable = false)
    private int memberId;  // ✅ int(11)

    @Column(name = "car_id", nullable = false)
    private int carId;     // ✅ int(11)

    public enum Status { 판매중, 판매완료, 삭제, 판매대기, 철회 }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
