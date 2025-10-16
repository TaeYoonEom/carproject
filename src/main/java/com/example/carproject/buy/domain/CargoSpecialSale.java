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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "load_capacity_ton", precision = 3, scale = 1)
    private BigDecimal loadCapacityTon;  // 적재톤수

    @Enumerated(EnumType.STRING)
    @Column(name = "axle_config", columnDefinition = "ENUM('전축','후축','추축','없음')")
    private AxleConfig axleConfig;   // 축 구성

    private Integer year;
    private Integer month;
    private Integer mileage;
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "encar_diagnosis", columnDefinition = "ENUM('엔카진단','미진단')")
    private EncarDiagnosis encarDiagnosis;

    @Column(length = 100)
    private String region; //지역

    @Enumerated(EnumType.STRING)
    @Column(name = "performance_open", columnDefinition = "ENUM('전체 성능점검','성능기록부','미공개')")
    private PerformanceOpen performanceOpen;

    @Enumerated(EnumType.STRING)
    @Column(name = "seller_type", columnDefinition = "ENUM('일반','딜러')")
    private SellerType sellerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_type", columnDefinition = "ENUM('자가용','영업용','등본차량')")
    private UsageType usageType;

    @Column(length = 30)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", columnDefinition = "ENUM('휘발유','경유','LPG','전기','CNG','기타')")
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transmission", columnDefinition = "ENUM('오토','수동','세미오토','기타')")
    private Transmission transmission;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", referencedColumnName = "car_id")
    private AllCarSale allCarSale;

    // ====== ENUM 내부 클래스 ======
    public enum AxleConfig { 전축, 후축, 추축, 없음 }
    public enum EncarDiagnosis { 엔카진단, 미진단 }
    public enum PerformanceOpen { 전체_성능점검, 성능기록부, 미공개 }
    public enum SellerType { 일반, 딜러 }
    public enum UsageType { 자가용, 영업용, 특장차량 }
    public enum FuelType { 휘발유, 경유, LPG, 전기, CNG, 기타 }
    public enum Transmission { 오토, 수동, 세미오토, 기타 }
}
