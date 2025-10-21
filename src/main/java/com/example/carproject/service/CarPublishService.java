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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private final JdbcTemplate jdbc;

    /**
     * 초안(draftId)을 정식 매물로 발행:
     * - all_car_sale 생성/재사용
     * - origin=0 → car_sale / origin=1 → import_car_sale
     * - car_entry_draft 이미지 → car_image 자동 복사
     */
    @Transactional
    public Integer publishFromDraft(Integer draftId) {

        // 1️⃣ Draft 조회
        CarEntryDraft d = draftRepo.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("초안을 찾을 수 없습니다. id=" + draftId));

        // 2️⃣ AllCarSale 확보 (없으면 생성)
        AllCarSale all = allRepo.findByCarEntryDraftId(draftId)
                .orElseGet(() -> {
                    AllCarSale created = new AllCarSale();
                    created.setMemberId(d.getMemberId());
                    created.setCarEntryDraftId(d.getId());
                    created.setOrigin(d.getOrigin()); // 그대로 전달 (0=국산, 1=수입)
                    created.setIsEcoFriendly(Boolean.TRUE.equals(d.getIsEcoFriendly()));
                    created.setIsCargo(d.getCarType() != null && d.getCarType().contains("화물"));
                    return allRepo.save(created);
                });

        Integer carId = all.getCarId();

        // 3️⃣ 제조 연/월 계산
        Integer year = null, month = null;
        if (d.getManufactureDate() != null) {
            LocalDate md = d.getManufactureDate();
            year = md.getYear();
            month = md.getMonthValue();
        }

        // 4️⃣ 공통 매핑 값
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
        Integer price         = (d.getPrice() != null) ? d.getPrice() : 0;
        String performanceOpen= "성능기록부";
        String sellerType     = "개인";
        String ownershipStatus= "등록중";
        LocalDateTime createdAt = LocalDateTime.now();

        int originValue = (d.getOrigin() != null ? d.getOrigin() : 0);
        boolean isImport = (originValue == 1);

        // 5️⃣ 분기: 국산/수입 테이블 삽입
        if (!isImport) {
            if (!carSaleRepo.existsByCarId(carId)) {
                CarSale cs = new CarSale();
                cs.setCarId(carId);
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
                cs.setCapacity(0);
                cs.setSaleType(saleType);
                cs.setFuelType(fuelType);
                cs.setTransmission(transmission);
                cs.setPerformanceOpen(performanceOpen);
                cs.setSellerType(sellerType);
                cs.setSaleMethod(saleMethod != null ? saleMethod : "일반");
                carSaleRepo.saveAndFlush(cs); // ✅ 즉시 flush 보장
            }
        } else {
            if (!importRepo.existsByCarId(carId)) {
                ImportCarSale is = new ImportCarSale();
                is.setCarId(carId);
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
                is.setCapacity(0);
                is.setSaleType(saleType);
                is.setFuelType(fuelType);
                is.setTransmission(transmission);
                is.setPerformanceOpen(performanceOpen);
                is.setSellerType(sellerType);
                is.setSaleMethod(saleMethod != null ? saleMethod : "일반");
                importRepo.saveAndFlush(is); // ✅ 즉시 반영
            }
        }

        // 6️⃣ 이미지 복사
        copyDraftImagesToCarImage(d, carId);

        return carId;
    }

    private void copyDraftImagesToCarImage(CarEntryDraft draft, int carId) {
        Integer exists = jdbc.queryForObject(
                "SELECT COUNT(*) FROM car_image WHERE car_id = ?", Integer.class, carId);
        if (exists != null && exists > 0) return;

        String sql = """
            INSERT INTO car_image (
                car_id, is_representative, uploaded_at,
                front_view_url, left_side_url, right_side_url,
                rear_view_url, driver_seat_url, back_seat_url
            )
            VALUES (?, 1, NOW(), ?, ?, ?, ?, ?, ?)
        """;

        jdbc.update(sql,
                carId,
                draft.getFrontViewUrl(),
                draft.getLeftSideUrl(),
                draft.getRightSideUrl(),
                draft.getRearViewUrl(),
                draft.getDriverSeatUrl(),
                draft.getBackSeatUrl()
        );
    }
}
