package com.example.carproject.buy.domain;

import com.example.carproject.domain.AllCarSale;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cargo_special_sale")
public class CargoSpecialSale {

    @Id
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(length = 50)
    private String manufacturer;  // 제조사

    @Column(name = "model_name", length = 100)
    private String modelName;     // 모델명

    @Column(name = "body_type", length = 60)
    private String bodyType;      // 차체형식

    private BigDecimal loadCapacityTon;  // 적재톤수

    @Column(name = "axle_config", columnDefinition = "ENUM('전축','중축','후축','없음')")
    private String axleConfig;   // 축 구성

    private Integer year;
    private Integer month;
    private Integer mileage;
    private Integer price;

    @Column(name = "encar_diagnosis", columnDefinition = "ENUM('엔카진단','미진단')")
    private String encarDiagnosis;

    @Column(length = 100)
    private String region; //지역

    @Column(columnDefinition = "ENUM('직영 성능점검', '성능기록부', '보험이력', '차량 이력 공개')")
    private String performanceOpen;

    @Column(name = "seller_type", columnDefinition = "ENUM('개인','딜러')")
    private String sellerType;

    @Column(name = "usage_type", columnDefinition = "ENUM('자가용','영업용','등본차량')")
    private String usageType; //용도

    @Column(length = 30)
    private String color;

    @Column(name = "fuel_type", columnDefinition = "ENUM('가솔린','디젤','LPG','전기','CNG','기타')")
    private String fuelType;

    @Column(name = "transmission", columnDefinition = "ENUM('오토','수동','세미오토','기타')")
    private String transmission;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", referencedColumnName = "car_id")
    private AllCarSale allCarSale;

}
