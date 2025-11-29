package com.example.carproject.buy.repository;

import com.example.carproject.buy.domain.EcoCar;
import com.example.carproject.buy.dto.ElectricFilterRequest;
import com.example.carproject.domain.AllCarSale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface ElectricCarRepository
        extends JpaRepository<EcoCar, Integer>,
        JpaSpecificationExecutor<EcoCar>{

    /*@Query(value =
            " (SELECT cs.car_id AS carId, a.origin AS origin, cs.car_name AS carName, cs.price AS price, " +
                    "         cs.year AS year, cs.mileage AS mileage, cs.drive_type AS driveType, " +
                    "         cs.sale_location AS saleLocation, cs.ownership_status AS ownershipStatus, " +
                    "         COALESCE(ci.front_view_url, '/img/common/noimage.png') AS imageUrl " +
                    "    FROM all_car_sale a " +
                    "    JOIN car_sale cs ON (cs.all_car_sale_id = a.car_id OR cs.car_id = a.car_id) " +
                    "    LEFT JOIN car_image ci ON ci.car_id = a.car_id AND ci.is_representative = 1 " +
                    "   WHERE a.is_eco_friendly = 1) " +
                    " UNION ALL " +
                    " (SELECT ims.car_id AS carId, a.origin AS origin, ims.car_name AS carName, ims.price AS price, " +
                    "         ims.year AS year, ims.mileage AS mileage, ims.drive_type AS driveType, " +
                    "         ims.sale_location AS saleLocation, ims.ownership_status AS ownershipStatus, " +
                    "         COALESCE(ci.front_view_url, '/img/common/noimage.png') AS imageUrl " +
                    "    FROM all_car_sale a " +
                    "    JOIN import_car_sale ims ON (ims.all_car_sale_id = a.car_id OR ims.car_id = a.car_id) " +
                    "    LEFT JOIN car_image ci ON ci.car_id = a.car_id AND ci.is_representative = 1 " +
                    "   WHERE a.is_eco_friendly = 1) ",
            countQuery =
                    " SELECT COUNT(*) FROM ( " +
                            "   SELECT cs.car_id " +
                            "     FROM all_car_sale a JOIN car_sale cs " +
                            "       ON (cs.all_car_sale_id = a.car_id OR cs.car_id = a.car_id) " +
                            "    WHERE a.is_eco_friendly = 1 " +
                            "   UNION ALL " +
                            "   SELECT ims.car_id " +
                            "     FROM all_car_sale a JOIN import_car_sale ims " +
                            "       ON (ims.all_car_sale_id = a.car_id OR ims.car_id = a.car_id) " +
                            "    WHERE a.is_eco_friendly = 1 " +
                            " ) t ",
            nativeQuery = true)
    Page<ElectricCarRow> findEcoCars(
            ElectricFilterRequest filters,
            Pageable pageable
    );
*/
}
