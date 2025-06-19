package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "car_image")
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "car_id", nullable = false)
    private Integer carId;

    @Column(name = "is_representative")
    private Boolean isRepresentative;

    @Column(name = "uploaded_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime uploadedAt;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", insertable = false, updatable = false)
    private AllCarSale allCarSale;

}
