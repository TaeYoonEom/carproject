package com.example.carproject.repository;

import com.example.carproject.buy.domain.CargoSpecialSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoSpecialSaleRepository extends JpaRepository<CargoSpecialSale, Integer> {
}