package com.example.carproject.buy.repository;

import com.example.carproject.domain.AllCarSale; // <- 엔티티
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface AllCarSaleRepository extends Repository<AllCarSale, Integer> {

    @Query(value = """
        SELECT 
          s.car_id         AS id,
          COALESCE(d.car_name, i.car_name) AS carName,
          COALESCE(d.price,    i.price)    AS price,
          img.front_view_url                AS frontViewUrl
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
}
