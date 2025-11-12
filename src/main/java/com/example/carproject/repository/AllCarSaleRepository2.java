package com.example.carproject.repository;

import com.example.carproject.domain.AllCarSale;
import com.example.carproject.repository.WishMini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllCarSaleRepository2 extends JpaRepository<AllCarSale, Integer> {

    Optional<AllCarSale> findByCarId(Integer carId);

    // draftId -> car_id
    @Query(value = "SELECT car_id FROM all_car_sale WHERE car_entry_draft_id = :draftId LIMIT 1", nativeQuery = true)
    Optional<Integer> findCarIdByDraftId(@Param("draftId") Integer draftId);

    boolean existsByCarIdAndMemberId(int carId, int memberId);

    Optional<AllCarSale> findByCarEntryDraftId(Integer draftId);
    boolean existsByCarEntryDraftId(Integer draftId);

    // 찜 미니리스트
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

    // 멤버의 등록 차량 목록
    @Query(value = """
        SELECT *
        FROM all_car_sale
        WHERE member_id = :memberId
        ORDER BY car_id DESC
    """, nativeQuery = true)
    List<AllCarSale> findAllByMemberId(@Param("memberId") Integer memberId);

    // ✅ [신규] 판매중 카드 목록 (car_sold → all_car_sale → car_sale/import + 대표이미지)
    @Query(value = """
    SELECT 
        s.car_id AS carId,
        COALESCE(d.car_name, i.car_name) AS carName,
        COALESCE(d.car_number, i.car_number) AS carNumber,
        COALESCE(d.year, i.year) AS year,
        COALESCE(d.mileage, i.mileage) AS mileage,
        COALESCE(d.sale_location, i.sale_location) AS saleLocation,
        COALESCE(d.price, i.price) AS price,
        img.front_view_url AS frontViewUrl,
        sold.status AS status
    FROM car_sold sold
    JOIN all_car_sale s ON s.car_id = sold.car_id
    LEFT JOIN car_sale d ON d.car_id = s.car_id
    LEFT JOIN import_car_sale i ON i.car_id = s.car_id
    LEFT JOIN car_image img ON img.car_id = s.car_id
         AND (img.is_representative = 1 OR img.is_representative = TRUE)
    WHERE sold.member_id = :memberId
      AND sold.status = :status
    ORDER BY COALESCE(sold.updated_at, sold.created_at) DESC
""", nativeQuery = true)
    List<SellOnMini> findCarsByMemberAndStatus(@Param("memberId") Integer memberId,
                                               @Param("status") String status);

}
