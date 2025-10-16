package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.CargoSpecialSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoSpecialSaleRepository extends JpaRepository<CargoSpecialSale, Integer> {
    // 필요시 나중에 distinct/집계 메서드 추가
}
