package com.example.carproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "all_car_sale")
public class AllCarSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)          // ✅ AUTO_INCREMENT 맞추기
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "origin", columnDefinition = "TINYINT(1)")
    private Integer origin;   // 0=국산, 1=수입

    @Column(name = "is_eco_friendly")
    private Boolean isEcoFriendly;

    @Column(name = "is_cargo")
    private Integer isCargo;   // 🔥 Boolean → Integer 로 변경

    @Column(name = "car_entry_draft_id", unique = true)          // ✅ 서비스에서 세팅할 필드 추가
    private Integer carEntryDraftId;

    @OneToMany(mappedBy = "allCarSale", cascade = CascadeType.ALL)
    private java.util.List<CarImage> carImages;

    // 대표 이미지 반환 메서드
    public java.util.Optional<CarImage> getRepresentativeImage() {
        return carImages.stream()
                .filter(CarImage::getIsRepresentative)
                .findFirst();
    }

}
