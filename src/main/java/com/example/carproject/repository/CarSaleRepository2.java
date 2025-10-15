package com.example.carproject.repository;

import com.example.carproject.buy.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarSaleRepository2 extends JpaRepository<CarSale, Integer> {
    Optional<CarSale> findByCarId(Integer carId);
    boolean existsByCarId(Integer carId);
}
