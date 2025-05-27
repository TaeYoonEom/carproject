package com.example.carproject.service;

import com.example.carproject.domain.CarSale;
import com.example.carproject.repository.CarSaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarSaleService {

    private final CarSaleRepository carSaleRepository;

    public CarSaleService(CarSaleRepository carSaleRepository) {
        this.carSaleRepository = carSaleRepository;
    }

    public List<CarSale> getAllCars() {
        return carSaleRepository.findAll();
    }

//    public List<CarSale> findByCarType(String carType) { // 에러에러에러 주석처리
//        return carSaleRepository.findByCarType(carType);
//    }
}
