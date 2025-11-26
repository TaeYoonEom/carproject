package com.example.carproject.service;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.buy.domain.ImportCarSale;
import com.example.carproject.domain.AllCarSale;
import com.example.carproject.repository.SellOnMini;
import com.example.carproject.dto.ExportPriceCarDto;
import com.example.carproject.dto.ExportPriceResult;
import com.example.carproject.dto.MyCarSimpleDto;
import com.example.carproject.repository.AllCarSaleRepository2;
import com.example.carproject.repository.CarSaleRepository2;
import com.example.carproject.repository.CargoSpecialSaleRepository;
import com.example.carproject.repository.ImportCarSaleRepository2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExportPriceService {

    private final AllCarSaleRepository2 allRepo;
    private final CarSaleRepository2 carSaleRepo;
    private final ImportCarSaleRepository2 importRepo;
    private final CargoSpecialSaleRepository cargoRepo;

    /**
     *  🔥 차량 ID 기반으로 유사 차량 시세 조회
     *  - manufacturer 일치
     *  - model_name 포함 검색
     */
    public ExportPriceResult findSimilarCars(Integer carId) {

        AllCarSale all = allRepo.findByCarId(carId).orElseThrow();

        List<?> rawCars;

        // ======================
        // 화물차
        // ======================
        if (all.getIsCargo() == 1) {
            CargoSpecialSale my = cargoRepo.findById(carId).orElseThrow();
            rawCars = cargoRepo.findSimilar(my.getManufacturer(), my.getModelName());
        }

        // ======================
        // 국산 차량
        // ======================
        else if (all.getOrigin() == 0) {
            CarSale my = carSaleRepo.findByCarId(carId).orElseThrow();
            rawCars = carSaleRepo.findSimilar(my.getManufacturer(), my.getModelName());
        }

        // ======================
        // 수입 차량
        // ======================
        else {
            ImportCarSale my = importRepo.findByCarId(carId).orElseThrow();
            rawCars = importRepo.findSimilar(my.getManufacturer(), my.getModelName());
        }

        // DTO 변환
        List<ExportPriceCarDto> cars = rawCars.stream()
                .map(o -> {
                    if (o instanceof CarSale c) {
                        return new ExportPriceCarDto(
                                c.getCarId(),
                                c.getManufacturer(),
                                c.getModelName(),
                                c.getYear(),
                                c.getMileage(),
                                c.getPrice(),
                                c.getSaleLocation()
                        );
                    }
                    if (o instanceof ImportCarSale c) {
                        return new ExportPriceCarDto(
                                c.getCarId(),
                                c.getManufacturer(),
                                c.getModelName(),
                                c.getYear(),
                                c.getMileage(),
                                c.getPrice(),
                                c.getSaleLocation()
                        );
                    }
                    if (o instanceof CargoSpecialSale c) {
                        return new ExportPriceCarDto(
                                c.getCarId(),
                                c.getManufacturer(),
                                c.getModelName(),
                                c.getYear(),
                                c.getMileage(),
                                c.getPrice(),
                                c.getRegion()
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        // 가격 계산
        List<Integer> prices = cars.stream()
                .map(ExportPriceCarDto::getPrice)
                .sorted()
                .toList();

        int count = prices.size();
        int min = count > 0 ? prices.get(0) : 0;
        int max = count > 0 ? prices.get(count - 1) : 0;
        int avg = count > 0 ? (int) prices.stream().mapToInt(v -> v).average().orElse(0) : 0;

        return new ExportPriceResult(cars, count, min, max, avg);
    }


    /**
     * 🔥 내 판매중 차량 목록 (시세 검색용)
     */
    public List<MyCarSimpleDto> findMySellingCars(Integer memberId) {

        List<SellOnMini> rows = allRepo.findCarsByMemberAndStatus(memberId, "판매중");

        return rows.stream().map(r ->
                new MyCarSimpleDto(
                        r.getCarId(),
                        r.getCarName(),     // 풀 네임
                        r.getYear()
                )
        ).toList();
    }

}
