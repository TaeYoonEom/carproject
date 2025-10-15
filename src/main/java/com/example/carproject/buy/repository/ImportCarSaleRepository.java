// src/main/java/com/example/carproject/buy/repository/ImportCarSaleRepository.java
package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.ImportCarSale;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ImportCarSaleRepository extends JpaRepository<ImportCarSale, Integer> {

    @Query("""
        select distinct ics
        from ImportCarSale ics
        left join fetch ics.allCarSale ac
        left join fetch ac.carImages ci
    """)
    List<ImportCarSale> findAllWithImages();

    long countByCarType(String carType);
}
