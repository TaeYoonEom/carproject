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

    @Query("SELECT DISTINCT c.manufacturer FROM ImportCarSale c")
    List<String> findAllManufacturers();

    @Query("SELECT DISTINCT c.modelName FROM ImportCarSale c WHERE c.manufacturer = :maker")
    List<String> findModelsByMaker(String maker);

    @Query("SELECT DISTINCT c.year FROM ImportCarSale c WHERE c.manufacturer = :maker AND c.modelName = :model")
    List<Integer> findYears(String maker, String model);

    @Query("""
        SELECT c FROM ImportCarSale c
        WHERE c.manufacturer = :maker
          AND c.modelName = :model
          AND (:year IS NULL OR c.year = :year)
    """)
    List<ImportCarSale> searchExport(String maker, String model, Integer year);



}
