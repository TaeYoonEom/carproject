package com.example.carproject.service;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.domain.ImportCarSale;
import com.example.carproject.domain.AllCarSale;
import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.repository.AllCarSaleRepository2;
import com.example.carproject.repository.CarEntryDraftRepository;
import com.example.carproject.repository.CarSaleRepository2;
import com.example.carproject.repository.ImportCarSaleRepository2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CarPublishService {

    private final AllCarSaleRepository2 allRepo;
    private final CarEntryDraftRepository draftRepo;
    private final CarSaleRepository2 carSaleRepo;
    private final ImportCarSaleRepository2 importRepo;

    /**
     * 초안(draftId)을 정식 매물로 발행:
     * - all_car_sale 생성/재사용
     * - origin=false(국산) → car_sale
     * - origin=true(수입)  → import_car_sale
     * @return 생성/연결된 car_id
     */
    @Transactional
    public Integer publishFromDraft(Integer draftId) {

        // 1) Draft 조회
        CarEntryDraft d = draftRepo.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("초안을 찾을 수 없습니다. id=" + draftId));

        // 2) AllCarSale 확보 (없으면 생성)
        AllCarSale all = allRepo.findByCarEntryDraftId(draftId)
                .orElseGet(() -> {
                    AllCarSale created = new AllCarSale();
                    created.setMemberId(d.getMemberId());
                    created.setCarEntryDraftId(d.getId());
                    created.setOrigin(Boolean.TRUE.equals(d.getOrigin()) ? "수입" : "국산");
                    created.setIsEcoFriendly(Boolean.TRUE.equals(d.getIsEcoFriendly()));
                    created.setIsCargo(d.getCarType() != null && d.getCarType().contains("화물"));
                    return allRepo.save(created);
                });

        Integer carId = all.getCarId();

        // 3) 제조 연/월 계산
        Integer year = null, month = null;
        if (d.getManufactureDate() != null) {
            LocalDate md = d.getManufactureDate();
            year = md.getYear();
            month = md.getMonthValue();
        }

        // 4) 공통 매핑 값
        String carName        = (d.getModelName() != null && !d.getModelName().isBlank()) ? d.getModelName() : d.getModel();
        String manufacturer   = d.getManufacturer();
        String modelName      = (d.getModelName() != null) ? d.getModelName() : d.getModel();
        String carNumber      = d.getCarNumber();
        String ownerName      = d.getOwnerName();
        String deliveryOption = d.getDeliveryOption();
        String exteriorColor  = d.getExteriorColor();
        String interiorColor  = d.getInteriorColor();
        String seatColor      = d.getSeatColor();
        String driveType      = d.getDriveType();
        String saleLocation   = d.getRegion();
        Integer mileage       = d.getMileage();
        String carType        = d.getCarType();
        String carGrade       = d.getCarGrade();
        String saleType       = d.getSaleType();
        String fuelType       = d.getFuelType();
        String transmission   = d.getTransmission();
        String saleMethod     = d.getSaleMethod();
        Integer price         = 0;                       // draft에 없으면 0
        String performanceOpen= "성능기록부";            // 기본값
        String sellerType     = "개인";                  // 기본값
        String ownershipStatus= "등록중";                // 기본값
        LocalDateTime createdAt = LocalDateTime.now();

        boolean isImport = Boolean.TRUE.equals(d.getOrigin()); // true=수입, false=국산

        if (!isImport) {
            // === 국산: car_sale ===
            if (!carSaleRepo.existsByCarId(carId)) {
                CarSale cs = new CarSale();
                // 🔑 PK를 all_car_sale.car_id와 동일하게 맞춤 (현재 매핑에서는 명시 세팅이 필요)
                cs.setCarId(carId);
                // 🔗 연관관계는 엔티티로 세팅
                cs.setAllCarSale(all);

                cs.setMemberId(d.getMemberId());
                cs.setCarName(carName);
                cs.setManufacturer(manufacturer);
                cs.setModelName(modelName);
                cs.setCarNumber(carNumber);
                cs.setOwnershipStatus(ownershipStatus);
                cs.setOwnerName(ownerName);
                cs.setDeliveryOption(deliveryOption);
                cs.setExteriorColor(exteriorColor);
                cs.setInteriorColor(interiorColor);
                cs.setSeatColor(seatColor);
                cs.setDriveType(driveType);
                cs.setSaleLocation(saleLocation);
                cs.setPrice(price);
                cs.setMileage(mileage);
                cs.setYear(year != null ? year : 0);
                cs.setMonth(month != null ? month : 0);
                cs.setCreatedAt(createdAt);
                cs.setCarType(carType);
                cs.setCarGrade(carGrade);
                cs.setCapacity(0); // Draft에 값 없으므로 0 또는 null 허용 시 엔티티 타입을 Integer로
                cs.setSaleType(saleType);
                cs.setFuelType(fuelType);
                cs.setTransmission(transmission);
                cs.setPerformanceOpen(performanceOpen);
                cs.setSellerType(sellerType);
                cs.setSaleMethod(saleMethod != null ? saleMethod : "일반");
                carSaleRepo.save(cs);
            }
        } else {
            // === 수입: import_car_sale ===
            if (!importRepo.existsByCarId(carId)) {
                ImportCarSale is = new ImportCarSale();
                // 🔑 PK를 all_car_sale.car_id와 동일하게
                is.setCarId(carId);
                // 🔗 연관관계는 엔티티로 세팅
                is.setAllCarSale(all);

                is.setMemberId(d.getMemberId());
                is.setCarName(carName);
                is.setManufacturer(manufacturer);
                is.setModelName(modelName);
                is.setCarNumber(carNumber);
                is.setOwnershipStatus(ownershipStatus);
                is.setOwnerName(ownerName);
                is.setDeliveryOption(deliveryOption);
                is.setExteriorColor(exteriorColor);
                is.setInteriorColor(interiorColor);
                is.setSeatColor(seatColor);
                is.setDriveType(driveType);
                is.setSaleLocation(saleLocation);
                is.setPrice(price);
                is.setMileage(mileage);
                is.setYear(year != null ? year : 0);
                is.setMonth(month != null ? month : 0);
                is.setCreatedAt(createdAt);
                is.setCarType(carType);
                is.setCarGrade(carGrade);
                is.setCapacity(0); // 필요 시 Integer로 변경
                is.setSaleType(saleType);
                is.setFuelType(fuelType);
                is.setTransmission(transmission);
                is.setPerformanceOpen(performanceOpen);
                is.setSellerType(sellerType);
                is.setSaleMethod(saleMethod != null ? saleMethod : "일반");
                importRepo.save(is);
            }
        }

        return carId;
    }
}
