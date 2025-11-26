package com.example.carproject.repository;

import com.example.carproject.buy.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarSaleRepository2 extends JpaRepository<CarSale, Integer> {
    Optional<CarSale> findByCarId(Integer carId);
    boolean existsByCarId(Integer carId);

    @Query("""
    SELECT c FROM CarSale c
    WHERE LOWER(c.manufacturer) LIKE LOWER(CONCAT('%', :manu, '%'))
      AND (
           LOWER(c.modelName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(c.carName) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
    """)
    List<CarSale> findSimilar(
            @Param("manu") String manu,
            @Param("keyword") String keyword);

    // ▼ 제조사 목록
    @Query("SELECT DISTINCT c.manufacturer FROM CarSale c")
    List<String> findAllManufacturers();

    // ▼ 모델 목록
    @Query("SELECT DISTINCT c.modelName FROM CarSale c WHERE c.manufacturer = :maker")
    List<String> findModelsByMaker(String maker);

    // ▼ 연식 목록
    @Query("SELECT DISTINCT c.year FROM CarSale c WHERE c.manufacturer = :maker AND c.modelName = :model")
    List<Integer> findYears(String maker, String model);

    // ▼ 검색
    @Query("""
        SELECT c FROM CarSale c
        WHERE c.manufacturer = :maker
          AND c.modelName = :model
          AND (:year IS NULL OR c.year = :year)
    """)
    List<CarSale> searchExport(String maker, String model, Integer year);



}
