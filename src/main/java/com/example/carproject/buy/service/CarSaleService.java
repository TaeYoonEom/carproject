package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.repository.CarSaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
