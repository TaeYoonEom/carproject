package com.example.carproject.repository;

import com.example.carproject.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarSaleRepository extends JpaRepository<CarSale, Long> {
    // 차량 관련 커스텀 쿼리도 여기에 추가 가능
}
