package com.example.carproject.repository;

import com.example.carproject.domain.AllCarSale;
import com.example.carproject.repository.WishMini; // WishMini 프로젝션 패키지 경로 유지
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllCarSaleRepository2 extends JpaRepository<AllCarSale, Integer> {

    // 1) draftId -> car_id (car_detail 이동용)
    @Query(value = "SELECT car_id FROM all_car_sale WHERE car_entry_draft_id = :draftId LIMIT 1", nativeQuery = true)
    Optional<Integer> findCarIdByDraftId(@Param("draftId") Integer draftId);

    // 2) 본인 차량 소유 여부 확인
    boolean existsByCarIdAndMemberId(int carId, int memberId);

    // 3) 초안 기반 멱등 체크
    Optional<AllCarSale> findByCarEntryDraftId(Integer draftId);
    boolean existsByCarEntryDraftId(Integer draftId);

    // 4) 마이페이지 '찜한 차량' 미니리스트 (네이티브 + 프로젝션)
    @Query(value = """
        SELECT 
          s.car_id                                           AS id,
          COALESCE(d.car_name, i.car_name)                  AS carName,
          COALESCE(d.price,    i.price)                     AS price,
          img.front_view_url                                AS frontViewUrl
        FROM car_purchase_wishlist w
        JOIN all_car_sale s         ON s.car_id = w.car_id
        LEFT JOIN car_sale d        ON d.car_id = s.car_id
        LEFT JOIN import_car_sale i ON i.car_id = s.car_id
        LEFT JOIN car_image img     ON img.car_id = s.car_id
                                    AND (img.is_representative = 1 OR img.is_representative = TRUE)
        WHERE w.member_id = :memberId
        ORDER BY w.created_at DESC, s.car_id DESC
    """, nativeQuery = true)
    List<WishMini> findWishAll(@Param("memberId") Integer memberId);

    // 5) 특정 멤버의 등록 차량 목록 (네이티브로 안전하게)
    @Query(value = """
        SELECT *
        FROM all_car_sale
        WHERE member_id = :memberId
        ORDER BY car_id DESC
    """, nativeQuery = true)
    List<AllCarSale> findAllByMemberId(@Param("memberId") Integer memberId);
}
