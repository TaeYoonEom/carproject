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

import java.util.*;
import java.util.stream.Stream;

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

    public List<String> findAllManufacturers() {
        List<String> a = carSaleRepo.findAllManufacturers();
        List<String> b = importRepo.findAllManufacturers();
        List<String> c = cargoRepo.findAllManufacturers();

        return Stream.concat(a.stream(),
                        Stream.concat(b.stream(), c.stream()))
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> findModelsByMaker(String maker) {
        List<String> a = carSaleRepo.findModelsByMaker(maker);
        List<String> b = importRepo.findModelsByMaker(maker);
        List<String> c = cargoRepo.findModelsByMaker(maker);

        return Stream.concat(a.stream(),
                        Stream.concat(b.stream(), c.stream()))
                .distinct()
                .sorted()
                .toList();
    }

    public List<Integer> findYears(String maker, String model) {
        List<Integer> a = carSaleRepo.findYears(maker, model);
        List<Integer> b = importRepo.findYears(maker, model);
        List<Integer> c = cargoRepo.findYears(maker, model);

        return Stream.concat(a.stream(),
                        Stream.concat(b.stream(), c.stream()))
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    public List<ExportPriceCarDto> findCarsByFilter(String maker, String model, Integer year) {

        List<?> raw1 = carSaleRepo.searchExport(maker, model, year);
        List<?> raw2 = importRepo.searchExport(maker, model, year);
        List<?> raw3 = cargoRepo.searchExport(maker, model, year);

        List<Object> raw = Stream.concat(raw1.stream(),
                        Stream.concat(raw2.stream(), raw3.stream()))
                .toList();

        return raw.stream().map(o -> {
            if (o instanceof CarSale c) {
                return new ExportPriceCarDto(c.getCarId(), c.getManufacturer(), c.getModelName(),
                        c.getYear(), c.getMileage(), c.getPrice(), c.getSaleLocation());
            }
            if (o instanceof ImportCarSale c) {
                return new ExportPriceCarDto(c.getCarId(), c.getManufacturer(), c.getModelName(),
                        c.getYear(), c.getMileage(), c.getPrice(), c.getSaleLocation());
            }
            if (o instanceof CargoSpecialSale c) {
                return new ExportPriceCarDto(c.getCarId(), c.getManufacturer(), c.getModelName(),
                        c.getYear(), c.getMileage(), c.getPrice(), c.getRegion());
            }
            return null;
        }).filter(Objects::nonNull).toList();
    }

    public Map<String, List<String>> findManufacturersGrouped() {

        List<String> domestic = carSaleRepo.findAllManufacturers();   // 국산
        List<String> importList = importRepo.findAllManufacturers();  // 수입
        List<String> cargo = cargoRepo.findAllManufacturers();        // 화물/특장

        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put("국산차", domestic.stream().sorted().toList());
        map.put("수입차", importList.stream().sorted().toList());
        map.put("화물·특장", cargo.stream().sorted().toList());

        return map;
    }



}
