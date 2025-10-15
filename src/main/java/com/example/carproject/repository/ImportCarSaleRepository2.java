package com.example.carproject.repository;

import com.example.carproject.buy.domain.ImportCarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImportCarSaleRepository2 extends JpaRepository<ImportCarSale, Integer> {
    Optional<ImportCarSale> findByCarId(Integer carId);
    boolean existsByCarId(Integer carId);
}
