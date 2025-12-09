// src/main/java/com/example/carproject/buy/repository/ImportCarSaleRepository.java
package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.ImportCarSale;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImportCarSaleRepository extends JpaRepository<ImportCarSale, Integer>, JpaSpecificationExecutor<ImportCarSale> {
    interface FacetAgg{
        String getVal();
        long getCnt();
    }
    //제조사
    @Query("""
      select c.modelName as val, count(c) as cnt
      from ImportCarSale c
      where c.manufacturer = :maker and c.modelName is not null
      group by c.modelName
    """)
    List<FacetAgg> countModelsByMaker(@Param("maker") String maker);

    // ❷ 추가: 제조사 + 모델명별 차명(등급)
    @Query("""
      select c.carName as val, count(c) as cnt
      from ImportCarSale c
      where c.manufacturer = :maker
        and c.modelName = :model
        and c.carName is not null
      group by c.carName
    """)
    List<FacetAgg> countCarNamesByMakerAndModel(@Param("maker") String maker,
                                                @Param("model") String model);
    @Query("select distinct c.manufacturer from ImportCarSale c where c.manufacturer is not null")
    List<String> distinctManufacturers();

    @Query("select distinct c.modelName from ImportCarSale c where c.modelName is not null")
    List<String> distinctModelNames();

    @Query("select distinct c.carName from ImportCarSale c where c.carName is not null")
    List<String> distinctCarNames();

    @Query("select distinct c.performanceOpen from ImportCarSale c where c.performanceOpen is not null")
    List<String> distinctPerformanceOpen();

    @Query("select distinct c.fuelType from ImportCarSale c where c.fuelType is not null")
    List<String> distinctFuelTypes();

    @Query("select distinct c.transmission from ImportCarSale c where c.transmission is not null")
    List<String> distinctTransmissions();

    @Query("select distinct c.sellerType from ImportCarSale c where c.sellerType is not null")
    List<String> distinctSellerTypes();

    @Query("select distinct c.saleMethod from ImportCarSale c where c.saleMethod is not null")
    List<String> distinctSaleMethods();

    @Query("select distinct c.saleLocation from ImportCarSale c where c.saleLocation is not null")
    List<String> distinctSaleLocations();

    @Query("select distinct c.exteriorColor from ImportCarSale c where c.exteriorColor is not null")
    List<String> distinctExteriorColors();

    @Query("select distinct c.interiorColor from ImportCarSale c where c.interiorColor is not null")
    List<String> distinctInteriorColors();

    @Query("select distinct c.carType from ImportCarSale c where c.carType is not null")
    List<String> distinctCarTypes();

    @Query("select c.manufacturer as val, count(c) as cnt from ImportCarSale c where c.manufacturer is not null group by c.manufacturer")
    List<FacetAgg> countByManufacturer();

    @Query("select c.modelName as val, count(c) as cnt from ImportCarSale c where c.modelName is not null group by c.modelName")
    List<FacetAgg> countByModelName();

    @Query("select c.carName as val, count(c) as cnt from ImportCarSale c where c.carName is not null group by c.carName")
    List<FacetAgg> countByCarName();

    @Query("select c.fuelType as val, count(c) as cnt from ImportCarSale c where c.fuelType is not null group by c.fuelType")
    List<FacetAgg> countByFuelType();

    @Query("select c.transmission as val, count(c) as cnt from ImportCarSale c where c.transmission is not null group by c.transmission")
    List<FacetAgg> countByTransmission();

    @Query("select c.sellerType as val, count(c) as cnt from ImportCarSale c where c.sellerType is not null group by c.sellerType")
    List<FacetAgg> countBySellerType();

    @Query("select c.saleMethod as val, count(c) as cnt from ImportCarSale c where c.saleMethod is not null group by c.saleMethod")
    List<FacetAgg> countBySaleMethod();

    @Query("select c.saleLocation as val, count(c) as cnt from ImportCarSale c where c.saleLocation is not null group by c.saleLocation")
    List<FacetAgg> countBySaleLocation();

    @Query("select c.exteriorColor as val, count(c) as cnt from ImportCarSale c where c.exteriorColor is not null group by c.exteriorColor")
    List<FacetAgg> countByExteriorColor();

    @Query("select c.interiorColor as val, count(c) as cnt from ImportCarSale c where c.interiorColor is not null group by c.interiorColor")
    List<FacetAgg> countByInteriorColor();

    @Query("select c.carType as val, count(c) as cnt from ImportCarSale c where c.carType is not null group by c.carType")
    List<FacetAgg> countByCarType();

    @Query("select c.performanceOpen as val, count(c) as cnt from ImportCarSale c where c.performanceOpen is not null group by c.performanceOpen")
    List<FacetAgg> countByPerformanceOpen();

    //인승
    interface CapacityBuckets {
        long getLe2();
        long getEq3();
        long getEq4();
        long getEq5();
        long getEq6();
        long getEq7();
        long getEq8();
        long getEq9();
        long getGe10();
    }

    @Query("""
    select
      sum(case when c.capacity <= 2 then 1 else 0 end) as le2,
      sum(case when c.capacity = 3 then 1 else 0 end) as eq3,
      sum(case when c.capacity = 4 then 1 else 0 end) as eq4,
      sum(case when c.capacity = 5 then 1 else 0 end) as eq5,
      sum(case when c.capacity = 6 then 1 else 0 end) as eq6,
      sum(case when c.capacity = 7 then 1 else 0 end) as eq7,
      sum(case when c.capacity = 8 then 1 else 0 end) as eq8,
      sum(case when c.capacity = 9 then 1 else 0 end) as eq9,
      sum(case when c.capacity >= 10 then 1 else 0 end) as ge10
    from ImportCarSale c
    """)
    CapacityBuckets capacityBuckets();

    //수입차 이미지
    @Query("""
        select distinct ics
        from ImportCarSale ics
        left join fetch ics.allCarSale ac
        left join fetch ac.carImages ci
    """)
    List<ImportCarSale> findAllWithImages();

    long countByCarType(String carType);
}
