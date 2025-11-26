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



}
