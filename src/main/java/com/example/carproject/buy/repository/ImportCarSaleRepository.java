package com.example.carproject.buy.repository;

import com.example.carproject.importcar.domain.ImportCarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportCarSaleRepository extends JpaRepository<ImportCarSale, Integer> {
    List<ImportCarSale> findByCarType(String carType);  // "수입"
}