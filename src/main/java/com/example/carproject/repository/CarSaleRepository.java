package com.example.carproject.repository;

import com.example.carproject.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarSaleRepository extends JpaRepository<CarSale, Integer> {
    List<CarSale> findByCarType(String carType);
}
