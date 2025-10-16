package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.CargoSpecialSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckSaleRepository extends JpaRepository<CargoSpecialSale, Integer> {
    // 추후 필터/집계 쿼리 추가 가능
}
