package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.buy.dto.TruckCardDto;
import com.example.carproject.buy.dto.TruckFilterRequest;
import com.example.carproject.buy.repository.TruckSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TruckSaleService {

    private final TruckSaleRepository repo;

    // 전체 화물차 리스트 반환
    public List<CargoSpecialSale> getAll() {
        return repo.findAll();
    }

    // DTO 변환 (국산차 패턴 동일)
    public List<TruckCardDto> getTruckCards() {
        return repo.findAll().stream()
                .map(TruckCardDto::new)
                .toList();
    }

    public long getTruckCount() {
        return repo.count();
    }

    public Map<String, Object> buildFilters(String maker, String model) {

        Map<String, Object> map = new HashMap<>();
        // 단독 필터
        map.put("bodyTypes", repo.countByBodyType());
        map.put("loadCapacityTon", repo.countByLoadCapacityTon());
        map.put("axleConfig", repo.countByAxleConfig());
        map.put("region", repo.countByRegion());
        map.put("performanceOpen", repo.countByPerformance());
        map.put("sellerType", repo.countBySellerType());
        map.put("usageType", repo.countByUsageType());
        map.put("color", repo.countByColor());
        map.put("fuelType", repo.countByFuelType());
        map.put("transmission", repo.countByTransmission());

        //  제조사 / 모델
        map.put("manufacturer", repo.countByManufacturer());
        map.put("modelName", repo.findModelsByMaker(maker));

        map.put("carName", repo.findCarNamesByModel(model));

        return map;
    }
    /*public Page<TruckCardDto> searchWithFilters(
            TruckFilterRequest req,
            String sortKey,
            int page,
            int size
    ) {
        Specification<CargoSpecialSale> spec = Specification.where(null);

        // 체크박스 필터들
        if (req.getBodyType() != null && !req.getBodyType().isEmpty())
            spec = spec.and(inSpec("bodyType", req.getBodyType()));
        if (req.getManufacturer() != null && !req.getManufacturer().isEmpty())
            spec = spec.and(inSpec("manufacturer", req.getManufacturer()));
        if (req.getModelName() != null && !req.getModelName().isEmpty())
            spec = spec.and(inSpec("modelName", req.getModelName()));
        if (req.getAxleConfig() != null && !req.getAxleConfig().isEmpty())
            spec = spec.and(inSpec("axleConfig", req.getAxleConfig()));
        if (req.getRegion() != null && !req.getRegion().isEmpty())
            spec = spec.and(inSpec("region", req.getRegion()));
        if (req.getPerformanceOpen() != null && !req.getPerformanceOpen().isEmpty())
            spec = spec.and(inSpec("performanceOpen", req.getPerformanceOpen()));
        if (req.getSellerType() != null && !req.getSellerType().isEmpty())
            spec = spec.and(inSpec("sellerType", req.getSellerType()));
        if (req.getUsageType() != null && !req.getUsageType().isEmpty())
            spec = spec.and(inSpec("usageType", req.getUsageType()));
        if (req.getColor() != null && !req.getColor().isEmpty())
            spec = spec.and(inSpec("color", req.getColor()));
        if (req.getFuelType() != null && !req.getFuelType().isEmpty())
            spec = spec.and(inSpec("fuelType", req.getFuelType()));
        if (req.getTransmission() != null && !req.getTransmission().isEmpty())
            spec = spec.and(inSpec("transmission", req.getTransmission()));

        // 연식
        if (req.getYearFrom() != null && req.getMonthFrom() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.or(
                            cb.greaterThan(root.get("year"), req.getYearFrom()),
                            cb.and(
                                    cb.equal(root.get("year"), req.getYearFrom()),
                                    cb.greaterThanOrEqualTo(root.get("month"), req.getMonthFrom())
                            )
                    ));
        }

        if (req.getYearTo() != null && req.getMonthTo() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.or(
                            cb.lessThan(root.get("year"), req.getYearTo()),
                            cb.and(
                                    cb.equal(root.get("year"), req.getYearTo()),
                                    cb.lessThanOrEqualTo(root.get("month"), req.getMonthTo())
                            )
                    ));
        }

        // 주행거리
        if (req.getMileageMin() != null)
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("mileage"), req.getMileageMin()));

        if (req.getMileageMax() != null)
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("mileage"), req.getMileageMax()));

        // 가격
        if (req.getPriceMin() != null)
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), req.getPriceMin() * 10000));

        if (req.getPriceMax() != null)
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), req.getPriceMax() * 10000));

        // 정렬
        Sort sort = switch (sortKey) {
            case "priceAsc"    -> Sort.by("price").ascending();
            case "priceDesc"   -> Sort.by("price").descending();
            case "mileageAsc"  -> Sort.by("mileage").ascending();
            case "mileageDesc" -> Sort.by("mileage").descending();
            case "yearDesc"    -> Sort.by("year").descending();
            default            -> Sort.by("createdAt").descending();
        };

        PageRequest pageable = PageRequest.of(page, size, sort);

        Page<CargoSpecialSale> result = repo.findAll(spec, pageable);

        return result.map(TruckCardDto::new);
    }*/

    /*public List<TruckCardDto> filterTrucks(TruckFilterRequest req) {

        Specification<CargoSpecialSale> spec = Specification.where(null);

        // ===== 체크박스들 =====
        if (req.getBodyType() != null && !req.getBodyType().isEmpty())
            spec = spec.and(inSpec("bodyType", req.getBodyType()));

        if (req.getManufacturer() != null && !req.getManufacturer().isEmpty())
            spec = spec.and(inSpec("manufacturer", req.getManufacturer()));

        if (req.getModelName() != null && !req.getModelName().isEmpty())
            spec = spec.and(inSpec("modelName", req.getModelName()));

        if (req.getAxleConfig() != null && !req.getAxleConfig().isEmpty())
            spec = spec.and(inSpec("axleConfig", req.getAxleConfig()));

        if (req.getRegion() != null && !req.getRegion().isEmpty())
            spec = spec.and(inSpec("region", req.getRegion()));

        if (req.getPerformanceOpen() != null && !req.getPerformanceOpen().isEmpty())
            spec = spec.and(inSpec("performanceOpen", req.getPerformanceOpen()));

        if (req.getSellerType() != null && !req.getSellerType().isEmpty())
            spec = spec.and(inSpec("sellerType", req.getSellerType()));

        if (req.getUsageType() != null && !req.getUsageType().isEmpty())
            spec = spec.and(inSpec("usageType", req.getUsageType()));

        if (req.getColor() != null && !req.getColor().isEmpty())
            spec = spec.and(inSpec("color", req.getColor()));

        if (req.getFuelType() != null && !req.getFuelType().isEmpty())
            spec = spec.and(inSpec("fuelType", req.getFuelType()));

        if (req.getTransmission() != null && !req.getTransmission().isEmpty())
            spec = spec.and(inSpec("transmission", req.getTransmission()));


        // ===== 연식 =====
        if (req.getYearFrom() != null && req.getMonthFrom() != null) {
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.greaterThan(root.get("year"), req.getYearFrom()),
                    cb.and(
                            cb.equal(root.get("year"), req.getYearFrom()),
                            cb.greaterThanOrEqualTo(root.get("month"), req.getMonthFrom())
                    )
            ));
        }

        if (req.getYearTo() != null && req.getMonthTo() != null) {
            spec = spec.and((root, q, cb) -> cb.or(
                    cb.lessThan(root.get("year"), req.getYearTo()),
                    cb.and(
                            cb.equal(root.get("year"), req.getYearTo()),
                            cb.lessThanOrEqualTo(root.get("month"), req.getMonthTo())
                    )
            ));
        }

        // ===== 주행거리 =====
        if (req.getMileageMin() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("mileage"), req.getMileageMin())
            );
        }
        if (req.getMileageMax() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("mileage"), req.getMileageMax())
            );
        }

        // ===== 가격 =====
        if (req.getPriceMin() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), req.getPriceMin() * 10000)
            );
        }
        if (req.getPriceMax() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), req.getPriceMax() * 10000)
            );
        }

        List<CargoSpecialSale> rows = repo.findAll(spec);

        return rows.stream()
                .map(TruckCardDto::new)
                .toList();
    }

    // 공통 Specification
    private Specification<CargoSpecialSale> inSpec(String field, List<String> values) {
        return (root, query, cb) -> root.get(field).in(values);
    }

    // 엔카에서 내 차 찾기
    public List<TruckCardDto> filterByQuickSearch(String bodyType, String modelName, Integer capacity) {

        return repo.findAll().stream()
                .filter(t -> bodyType == null || bodyType.isBlank() || bodyType.equals(t.getBodyType()))
                .filter(t -> modelName == null || modelName.isBlank() ||
                        t.getModelName().startsWith(modelName))

                // 🔥 핵심 수정 부분
                .filter(t -> capacity == null ||
                        (t.getLoadCapacityTon() != null &&
                                t.getLoadCapacityTon().compareTo(BigDecimal.valueOf(capacity)) == 0)
                )
                .map(TruckCardDto::new)
                .toList();
    }

    public Map<String, Object> getFilters() {
        return buildFilters(null, null);
    }

    public List<String> getQuickBodyTypes() {
        return repo.findBodyTypesQuick();
    }

    public List<String> getQuickModels(String bodyType) {
        return repo.findModelNamesQuick(bodyType);
    }

    public List<Integer> getQuickCapacities(String modelName) {
        return repo.findCapacitiesQuick(modelName);
    }


*/
}
