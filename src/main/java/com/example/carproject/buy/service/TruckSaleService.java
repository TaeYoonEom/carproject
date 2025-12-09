package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.buy.dto.TruckCardDto;
import com.example.carproject.buy.dto.TruckFilterRequest;
import com.example.carproject.buy.repository.TruckSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private Specification<CargoSpecialSale> inSpec(String field, List<String> values) {
        return (root, q, cb) -> root.get(field).in(values);
    }

    private Specification<CargoSpecialSale> buildSpec(TruckFilterRequest req) {

        Specification<CargoSpecialSale> spec = Specification.where(null);

        if (req == null) return spec;

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

        // ================================
        // 연식 (year + month)
        // ================================
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

        // ================================
        // 주행거리
        // ================================
        if (req.getMileageMin() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("mileage"), req.getMileageMin()));
        }

        if (req.getMileageMax() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("mileage"), req.getMileageMax()));
        }

        // ================================
        // 가격 (만원 단위)
        // ================================
        if (req.getPriceMin() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), req.getPriceMin() * 10000));
        }

        if (req.getPriceMax() != null) {
            spec = spec.and((root, q, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), req.getPriceMax() * 10000));
        }

        return spec;
    }

    /* ===================================================================================
       3) 정렬 옵션 — import와 동일하게 정렬 키도 맞춰줌
    =================================================================================== */
    private Sort getSortOption(String sortKey) {
        return switch (sortKey) {
            case "priceAsc"    -> Sort.by("price").ascending();
            case "priceDesc"   -> Sort.by("price").descending();
            case "mileageAsc"  -> Sort.by("mileage").ascending();
            case "mileageDesc" -> Sort.by("mileage").descending();
            case "yearDesc"    -> Sort.by("year").descending().and(Sort.by("month").descending());
            default            -> Sort.by("createdAt").descending();
        };
    }

    /* ===================================================================================
       4) 최종 검색 메서드 — Controller 에서 그대로 사용 가능
    =================================================================================== */
    public Page<TruckCardDto> searchWithFilters(
            TruckFilterRequest req,
            String sortKey,
            int page,
            int size
    ) {
        Specification<CargoSpecialSale> spec = buildSpec(req);
        Pageable pageable = PageRequest.of(page, size, getSortOption(sortKey));

        return repo.findAll(spec, pageable)
                .map(TruckCardDto::new);
    }

    /* ===================================================================================
       5) Facet Filter(Count) 데이터 (엔카식 사이드바)
    =================================================================================== */
    public Map<String, Object> getFilterCounts(TruckFilterRequest filters) {
        Map<String, Object> map = new HashMap<>();

        String selectedMaker = null;
        if (filters != null && filters.getManufacturer() != null && !filters.getManufacturer().isEmpty()) {
            selectedMaker = filters.getManufacturer().get(0);
        }
        map.put("selectedMaker", selectedMaker);

        String selectedModel = null;
        if (filters != null && filters.getModelName() != null && !filters.getModelName().isEmpty()) {
            selectedModel = filters.getModelName().get(0);
        }
        map.put("selectedModel", selectedModel);

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

        // ✅ 2) 제조사 전체 목록
        map.put("manufacturer", repo.countByManufacturer());

        // ✅ 3) 선택된 제조사의 모델 리스트
        if (selectedMaker != null) {
            map.put("modelsForMaker", repo.findModelsByMaker(selectedMaker));  // FacetAgg 리스트
        }

        // ✅ 4) 선택된 모델의 세부모델(등급) 리스트
        if (selectedModel != null) {
            map.put("carNamesForModel", repo.findCarNamesByModel(selectedModel)); // FacetAgg 리스트
        }

        return map;
    }
}
