package com.example.carproject.repository;

import com.example.carproject.domain.CarPurchaseRecent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CarPurchaseRecentRepository extends JpaRepository<CarPurchaseRecent, Integer> {

    @Query(value = """
        SELECT *
        FROM car_purchase_recent
        WHERE member_id = :memberId
        ORDER BY viewed_at DESC
        LIMIT :limit
    """, nativeQuery = true)
    List<CarPurchaseRecent> findRecent(@Param("memberId") Integer memberId,
                                       @Param("limit") int limit);

    @Modifying
    @Query(value = """
        DELETE FROM car_purchase_recent
        WHERE member_id = :memberId 
          AND car_id = :carId
    """, nativeQuery = true)
    void deleteDuplicate(@Param("memberId") Integer memberId,
                         @Param("carId") Integer carId);

    // 🔥 최근 ID 목록 (오래된 순 정렬)
    @Query(value = """
        SELECT id
        FROM car_purchase_recent
        WHERE member_id = :memberId
        ORDER BY viewed_at DESC
    """, nativeQuery = true)
    List<Integer> findIdsOrdered(@Param("memberId") Integer memberId);

    // 🔥 여러 개 ID 삭제
    @Modifying
    @Query(value = """
        DELETE FROM car_purchase_recent
        WHERE id IN (:ids)
    """, nativeQuery = true)
    void deleteByIds(@Param("ids") List<Integer> ids);
}
