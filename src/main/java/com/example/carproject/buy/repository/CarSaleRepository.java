package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.CarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CarSaleRepository extends JpaRepository<CarSale, Integer> {
    @Query("select c.carType as carType, count(c) as cnt " +
            "from CarSale c group by c.carType")
    List<CarTypeAgg> countByCarType();

    interface CarTypeAgg {
        String getCarType();
        long getCnt();
    }
    // 선택된 차종(한글명) 목록으로 검색
    List<CarSale> findByCarTypeIn(List<String> carTypes);
    // ✅ 기타(ET): carType이 NULL 이거나, 알려진 셋에 없는 것들
    @Query("select c from CarSale c " +
            "where c.carType is null or c.carType not in :known")
    List<CarSale> findOthers(@Param("known") Collection<String> known);
    // 전체 국산차 목록
    List<CarSale> findByCarType(String carType);

    long countByCarType(String carType); //총 국산차 개수

}
