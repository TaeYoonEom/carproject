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
    private Long id;

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


}
