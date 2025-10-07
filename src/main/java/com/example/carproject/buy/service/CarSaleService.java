package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.dto.CarCardDto;
import com.example.carproject.buy.repository.CarSaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarSaleService {

    private final CarSaleRepository carSaleRepository;

    public CarSaleService(CarSaleRepository carSaleRepository) {
        this.carSaleRepository = carSaleRepository;
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
