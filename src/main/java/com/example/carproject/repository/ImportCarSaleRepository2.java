package com.example.carproject.repository;

import com.example.carproject.buy.domain.ImportCarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportCarSaleRepository2 extends JpaRepository<ImportCarSale, Integer> {
    Optional<ImportCarSale> findByCarId(Integer carId);
    Optional<ImportCarSale> findImportCarByCarId(Long carId);
    boolean existsByCarId(Integer carId);

    @Query("""
    SELECT c FROM ImportCarSale c
    WHERE LOWER(c.manufacturer) LIKE LOWER(CONCAT('%', :manu, '%'))
      AND (
           LOWER(c.modelName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.carName) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    """)
    List<ImportCarSale> findSimilar(
            @Param("manu") String manu,
            @Param("keyword") String keyword);



}
