package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CarSaleRepository extends JpaRepository<CarSale, Integer>, JpaSpecificationExecutor<CarSale> {
    interface FacetAgg {
        String getVal();
        long getCnt();
    }
    // 제조사별 model_name 카운트 (인기모델)
    @Query("""
      select c.modelName as val, count(c) as cnt
      from CarSale c
      where c.manufacturer = :maker and c.modelName is not null
      group by c.modelName
    """)
    List<FacetAgg> countModelsByMaker(@Param("maker") String maker);

    // 제조사별 car_name 카운트 (이름순 섹션에 표시할 개수)
    @Query("""
      select c.carName as val, count(c) as cnt
      from CarSale c
      where c.manufacturer = :maker and c.carName is not null
      group by c.carName
    """)
    List<FacetAgg> countCarNamesByMaker(@Param("maker") String maker);

    // 이름순 목록(알파벳/가나다 정렬용)
    @Query("""
      select distinct c.carName
      from CarSale c
      where c.manufacturer = :maker and c.carName is not null
      order by c.carName asc
    """)
    List<String> distinctCarNamesByMaker(@Param("maker") String maker);

    // 좌측 필터 옵션용 distinct 목록
    @Query("select distinct c.manufacturer from CarSale c where c.manufacturer is not null")
    List<String> distinctManufacturers();

    @Query("select distinct c.modelName from CarSale c where c.modelName is not null")
    List<String> distinctModelNames();

    @Query("select distinct c.carName from CarSale c where c.carName is not null")
    List<String> distinctCarNames();

    @Query("select distinct c.performanceOpen from CarSale c where c.performanceOpen is not null")
    List<String> distinctPerformanceOpen();

    @Query("select distinct c.fuelType from CarSale c where c.fuelType is not null")
    List<String> distinctFuelTypes();

    @Query("select distinct c.transmission from CarSale c where c.transmission is not null")
    List<String> distinctTransmissions();

    @Query("select distinct c.sellerType from CarSale c where c.sellerType is not null")
    List<String> distinctSellerTypes();

    @Query("select distinct c.saleMethod from CarSale c where c.saleMethod is not null")
    List<String> distinctSaleMethods();

    @Query("select distinct c.saleLocation from CarSale c where c.saleLocation is not null")
    List<String> distinctSaleLocations();

    @Query("select distinct c.driveType from CarSale c where c.driveType is not null")
    List<String> distinctDriveTypes();

    @Query("select distinct c.exteriorColor from CarSale c where c.exteriorColor is not null")
    List<String> distinctExteriorColors();

    @Query("select distinct c.interiorColor from CarSale c where c.interiorColor is not null")
    List<String> distinctInteriorColors();

    @Query("select distinct c.seatColor from CarSale c where c.seatColor is not null")
    List<String> distinctSeatColors();

    @Query("select distinct c.carType from CarSale c where c.carType is not null")
    List<String> distinctCarTypes();

    @Query("select c.manufacturer as val, count(c) as cnt from CarSale c where c.manufacturer is not null group by c.manufacturer order by cnt desc")
    List<FacetAgg> countByManufacturer();

    @Query("select c.modelName as val, count(c) as cnt from CarSale c where c.modelName is not null group by c.modelName order by cnt desc")
    List<FacetAgg> countByModelName();

    @Query("select c.carName as val, count(c) as cnt from CarSale c where c.carName is not null group by c.carName order by cnt desc")
    List<FacetAgg> countByCarName();

    @Query("select c.fuelType as val, count(c) as cnt from CarSale c where c.fuelType is not null group by c.fuelType order by cnt desc")
    List<FacetAgg> countByFuelType();

    @Query("select c.transmission as val, count(c) as cnt from CarSale c where c.transmission is not null group by c.transmission order by cnt desc")
    List<FacetAgg> countByTransmission();

    @Query("select c.sellerType as val, count(c) as cnt from CarSale c where c.sellerType is not null group by c.sellerType order by cnt desc")
    List<FacetAgg> countBySellerType();

    @Query("select c.saleMethod as val, count(c) as cnt from CarSale c where c.saleMethod is not null group by c.saleMethod order by cnt desc")
    List<FacetAgg> countBySaleMethod();

    @Query("select c.saleLocation as val, count(c) as cnt from CarSale c where c.saleLocation is not null group by c.saleLocation order by cnt desc")
    List<FacetAgg> countBySaleLocation();

    @Query("select c.exteriorColor as val, count(c) as cnt from CarSale c where c.exteriorColor is not null group by c.exteriorColor order by cnt desc")
    List<FacetAgg> countByExteriorColor();

    @Query("select c.interiorColor as val, count(c) as cnt from CarSale c where c.interiorColor is not null group by c.interiorColor order by cnt desc")
    List<FacetAgg> countByInteriorColor();

    @Query("select c.seatColor as val, count(c) as cnt from CarSale c where c.seatColor is not null group by c.seatColor order by cnt desc")
    List<FacetAgg> countBySeatColor();

    @Query("select c.carType as val, count(c) as cnt from CarSale c where c.carType is not null group by c.carType order by cnt desc")
    List<FacetAgg> countByCarType();

    @Query("select c.performanceOpen as val, count(c) as cnt from CarSale c where c.performanceOpen is not null group by c.performanceOpen order by cnt desc")
    List<FacetAgg> countByPerformanceOpen();

    @Query("select concat(c.capacity, '') as val, count(c) as cnt from CarSale c where c.capacity is not null group by c.capacity order by cnt desc")
    List<FacetAgg> countByCapacity();

    // 인승 버킷 집계 (<=2, =3..=9, >=10)
    public interface CapacityBuckets {
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
    from CarSale c
    """)
    CapacityBuckets capacityBuckets();

    // 전체 국산차 목록
    List<CarSale> findByCarType(String carType);

    long countByCarType(String carType); //총 국산차 개수

}
