package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarSaleRepository extends JpaRepository<CarSale, Integer> {
    List<CarSale> findByCarType(String carType);

    // ✅ 수입차 필터링
    List<CarSale> findBySaleType(String saleType);

    // ✅ 전기/하이브리드 등 포함 검색
    List<CarSale> findByFuelTypeContaining(String fuelType);
}
