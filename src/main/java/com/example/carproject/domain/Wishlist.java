package com.example.carproject.domain;

import jakarta.persistence.*;       // JPA 어노테이션
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;     // createdAt 필드

// Wishlist.java (타입만 Integer로 수정)
@Entity
@Table(name = "car_purchase_wishlist",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id","car_id"}))
@Getter @Setter
public class Wishlist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="member_id", nullable=false)
    private Integer memberId;

    @Column(name="car_id", nullable=false)
    private Integer carId;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
