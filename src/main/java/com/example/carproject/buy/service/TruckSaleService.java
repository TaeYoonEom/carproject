package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.buy.dto.TruckCardDto;
import com.example.carproject.buy.repository.TruckSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TruckSaleService {

    private final TruckSaleRepository repo;

    // 전체 화물차 리스트 반환
    public List<CargoSpecialSale> getAll() {
        return repo.findAll();
    }

    // DTO 변환 (국산차 패턴 동일)
    public List<TruckCardDto> getTruckCards() {
        return repo.findAll().stream()
                .map(TruckCardDto::new)
                .toList();
    }

    public long getTruckCount() {
        return repo.count();
    }

    public Map<String, Object> buildFilters(String maker, String model) {

        Map<String, Object> map = new HashMap<>();
        // 단독 필터
        map.put("bodyTypes", repo.countByBodyType());
        map.put("loadCapacityTon", repo.countByLoadCapacityTon());
        map.put("axleConfig", repo.countByAxleConfig());
        map.put("region", repo.countByRegion());
        map.put("performanceOpen", repo.countByPerformance());
        map.put("sellerType", repo.countBySellerType());
        map.put("usageType", repo.countByUsageType());
        map.put("color", repo.countByColor());
        map.put("fuelType", repo.countByFuelType());
        map.put("transmission", repo.countByTransmission());

        //  제조사 / 모델
        map.put("manufacturer", repo.countByManufacturer());
        map.put("modelName", repo.findModelsByMaker(maker));

        return map;
    }
}
