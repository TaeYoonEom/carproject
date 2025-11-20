package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.CargoSpecialSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface TruckSaleRepository extends JpaRepository<CargoSpecialSale, Integer>, JpaSpecificationExecutor<CargoSpecialSale> {
    interface FacetAgg {
        String getVal();
        Long getCnt();
    }
    @Query("""
        SELECT c.modelName AS val, COUNT(c) AS cnt
        FROM CargoSpecialSale c
        WHERE (:maker IS NULL OR c.manufacturer = :maker)
          AND c.modelName IS NOT NULL
        GROUP BY c.modelName
    """)
    List<FacetAgg> findModelsByMaker(@Param("maker") String maker);



    @Query("SELECT c.bodyType AS val, COUNT(c) AS cnt FROM CargoSpecialSale c  WHERE c.bodyType IS NOT NULL GROUP BY c.bodyType")
    List<FacetAgg> countByBodyType();

    @Query("""
        SELECT c.loadCapacityTon AS val, COUNT(c) AS cnt
        FROM CargoSpecialSale c
        WHERE c.loadCapacityTon IS NOT NULL
        GROUP BY c.loadCapacityTon
    """)
    List<FacetAgg> countByLoadCapacityTon();

    @Query("SELECT c.manufacturer AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.manufacturer")
    List<FacetAgg> countByManufacturer();

    @Query("SELECT c.modelName AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.modelName")
    List<FacetAgg> countByModelName();


    @Query("SELECT c.axleConfig AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.axleConfig")
    List<FacetAgg> countByAxleConfig();

    @Query("SELECT c.region AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.region")
    List<FacetAgg> countByRegion();

    @Query("SELECT c.performanceOpen AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.performanceOpen")
    List<FacetAgg> countByPerformance();

    @Query("SELECT c.sellerType AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.sellerType")
    List<FacetAgg> countBySellerType();

    @Query("SELECT c.usageType AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.usageType")
    List<FacetAgg> countByUsageType();

    @Query("SELECT c.color AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.color")
    List<FacetAgg> countByColor();

    @Query("SELECT c.fuelType AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.fuelType")
    List<FacetAgg> countByFuelType();

    @Query("SELECT c.transmission AS val, COUNT(c) AS cnt FROM CargoSpecialSale c GROUP BY c.transmission")
    List<FacetAgg> countByTransmission();
    
    // 엔카에서 내 차 찾기
    @Query("""
    SELECT c.modelName AS val, COUNT(c) AS cnt
    FROM CargoSpecialSale c
    WHERE (:model IS NULL OR c.modelName = :model)
      AND c.modelName IS NOT NULL
    GROUP BY c.modelName
""")
    List<FacetAgg> findCarNamesByModel(@Param("model") String model);

    @Query("SELECT DISTINCT c.bodyType FROM CargoSpecialSale c WHERE c.bodyType IS NOT NULL ORDER BY c.bodyType")
    List<String> findBodyTypesQuick();

    @Query("""
    SELECT DISTINCT SUBSTRING_INDEX(c.modelName, ' ', 1)
    FROM CargoSpecialSale c
    WHERE c.bodyType = :bodyType
    ORDER BY 1
    """)
    List<String> findModelNamesQuick(@Param("bodyType") String bodyType);


    @Query("""
SELECT DISTINCT c.loadCapacityTon
FROM CargoSpecialSale c
WHERE c.modelName LIKE CONCAT(:modelName, '%')
ORDER BY c.loadCapacityTon
""")
    List<Integer> findCapacitiesQuick(@Param("modelName") String modelName);




}

