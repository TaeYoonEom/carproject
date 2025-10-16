package com.example.carproject.buy.repository;

import com.example.carproject.buy.dto.CargoCardDto;
import com.example.carproject.buy.domain.CargoSpecialSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CargoSpecialSaleRepository extends JpaRepository<CargoSpecialSale, Integer> {

    @Query("""
        select new com.example.carproject.buy.dto.CargoCardDto(
            a.carId,
            concat(coalesce(c.manufacturer,''), ' ', coalesce(c.modelName,'')),
            c.year,
            c.mileage,
            coalesce(c.bodyType, cast(c.axleConfig as string)),
            c.region,
            c.price,
            coalesce(max(case when i.isRepresentative = true then i.frontViewUrl end), '/img/noimage.jpg'),
            '판매중'
        )
        from AllCarSale a
        join CargoSpecialSale c on c.carId = a.carId
        left join CarImage i on i.allCarSale = a
        where a.isCargo = true
        group by a.carId, c.manufacturer, c.modelName, c.year, c.mileage, c.bodyType, c.axleConfig, c.region, c.price
        order by a.carId desc
        """)
    List<CargoCardDto> findCargoCards();

    @Query("select count(a.carId) from AllCarSale a where a.isCargo = true")
    long countAllCargo();
}
