package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.dto.CarCardDto;
import com.example.carproject.buy.dto.FilterRequest;
import com.example.carproject.buy.repository.CarSaleRepository;
import com.example.carproject.buy.spec.CarSaleSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarSaleService {

    private final CarSaleRepository carSaleRepository;

    // ✅ 정렬 + 페이지네이션 + 필터 통합 메서드
    public Page<CarCardDto> searchWithFilter(FilterRequest req, int page, int size, String sort) {
        // 1️⃣ 정렬 기준 설정
        Sort sortOption;
        switch (sort) {
            case "priceAsc" -> sortOption = Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> sortOption = Sort.by(Sort.Direction.DESC, "price");
            case "mileageAsc" -> sortOption = Sort.by(Sort.Direction.ASC, "mileage");
            case "mileageDesc" -> sortOption = Sort.by(Sort.Direction.DESC, "mileage");
            case "yearDesc" -> sortOption = Sort.by(Sort.Direction.DESC, "year"); // ✅ 최신연식순
            default -> {// 🔸 최근등록순 보정: createdAt 없으면 carId DESC 사용
                try {
                    sortOption = Sort.by(Sort.Direction.DESC, "createdAt");
                } catch (Exception e) {
                    sortOption = Sort.by(Sort.Direction.DESC, "carId");
                }
            }
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortOption);

        // ✅ top 제조사 세트 전달 (기타 처리 위해)
        var topSet = Set.of("현대", "기아", "르노코리아", "쌍용", "쉐보레", "제네시스", "기타 제조사");

        // ✅ Specification + Pageable 조합
        Page<CarSale> carPage = carSaleRepository.findAll(CarSaleSpecs.from(req, topSet), pageable);

        // ✅ DTO 변환
        return carPage.map(CarCardDto::new);
    }
    // ✅ 전체 차량 조회
    public List<CarSale> getAllCars() {
        return carSaleRepository.findAll();
    }

    // ✅ 차량 유형별 검색
    public List<CarSale> findByCarType(String carType) {
        return carSaleRepository.findByCarType(carType);
    }
    public List<CarCardDto> getCarCardDtos() {
        return carSaleRepository.findAll().stream()
                .map(CarCardDto::new)
                .collect(Collectors.toList());
    }

    public long getAllCount() {
        return carSaleRepository.count();
    }

    // ✅ 🔥 새로 추가 (차종 필터 결과용 변환)
    public List<CarCardDto> toCardDtos(List<CarSale> cars) {
        if (cars == null || cars.isEmpty()) return List.of();
        return cars.stream()
                .map(CarCardDto::new)
                .collect(Collectors.toList());
    }
}
