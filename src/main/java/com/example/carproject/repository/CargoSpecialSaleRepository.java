package com.example.carproject.repository;

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


}