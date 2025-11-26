package com.example.carproject.repository;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.domain.CargoSpecialSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoSpecialSaleRepository extends JpaRepository<CargoSpecialSale, Integer> {
    @Query("""
    SELECT c FROM CargoSpecialSale c
    WHERE LOWER(c.manufacturer) LIKE LOWER(CONCAT('%', :manu, '%'))
      AND LOWER(c.modelName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<CargoSpecialSale> findSimilar(
            @Param("manu") String manu,
            @Param("keyword") String keyword);

    @Query("SELECT DISTINCT c.manufacturer FROM CargoSpecialSale c")
    List<String> findAllManufacturers();

    @Query("SELECT DISTINCT c.modelName FROM CargoSpecialSale c WHERE c.manufacturer = :maker")
    List<String> findModelsByMaker(String maker);

    @Query("SELECT DISTINCT c.year FROM CargoSpecialSale c WHERE c.manufacturer = :maker AND c.modelName = :model")
    List<Integer> findYears(String maker, String model);

    @Query("""
        SELECT c FROM CargoSpecialSale c
        WHERE c.manufacturer = :maker
          AND c.modelName = :model
          AND (:year IS NULL OR c.year = :year)
    """)
    List<CargoSpecialSale> searchExport(String maker, String model, Integer year);
}