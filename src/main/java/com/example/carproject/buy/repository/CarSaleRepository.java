package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarSaleRepository extends JpaRepository<CarSale, Integer> {
    List<CarSale> findByCarType(String carType);

    long countByCarType(String carType); //총 국산차 개수

}
