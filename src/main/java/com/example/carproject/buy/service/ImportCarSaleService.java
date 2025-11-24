package com.example.carproject.buy.service;

import com.example.carproject.buy.dto.ImportCarCardDto;
import com.example.carproject.buy.domain.ImportCarSale;
import com.example.carproject.buy.dto.ImportFilterRequest;
import com.example.carproject.buy.repository.ImportCarSaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImportCarSaleService {

    private final ImportCarSaleRepository repo;

    //이미지 Dto 변환
    public List<ImportCarCardDto> getCardDtos() {
        return repo.findAllWithImages()
                .stream()
                .map(ImportCarCardDto::new)
                .toList();
    }

    //총 개수
    public long getAllCount() {
        return repo.count();
    }

    // 헬퍼 메서드
    private List<Map<String, Object>> toList(List<String> keys, Map<String, Long> db) {
        List<Map<String, Object>> list = new java.util.ArrayList<>();
        for (String t : keys) {
            Map<String, Object> m = new HashMap<>();
            m.put("val", t);
            m.put("cnt", db.getOrDefault(t, 0L));
            list.add(m);
        }
        return list;
    }

    //필터용
    public Map<String, Object> getFilterCounts() {
        Map<String, Object> map = new HashMap<>();

        // 차종 원본 데이터
        List<ImportCarSaleRepository.FacetAgg> raw = repo.countByCarType();

        // DB 값 map (carType → count)
        Map<String, Long> dbMap = new HashMap<>();
        for (var r : raw) dbMap.put(r.getVal(), r.getCnt());

        // 기준 그룹 정의
        List<String> g1 = List.of("경차", "소형차", "준중형차", "중형차", "대형차", "스포츠카");
        List<String> g2 = List.of("SUV", "RV");
        List<String> g3 = List.of("경승합차", "승합차", "화물차");

        // 결과 그룹
        Map<String, List<Map<String, Object>>> groups = new LinkedHashMap<>();

        groups.put("승용", toList(g1, dbMap));
        groups.put("SUV/RV", toList(g2, dbMap));
        groups.put("승합/화물", toList(g3, dbMap));
        //기타
        long etcCount = 0L;

        for (String type : dbMap.keySet()) {

            boolean isEtc =
                    !g1.contains(type) &&
                            !g2.contains(type) &&
                            !g3.contains(type);

            if (isEtc) {
                etcCount += dbMap.get(type);
            }
        }

        // 기타를 단 하나의 항목으로만 만든다
        groups.put("기타", List.of(
                Map.of("val", "기타", "cnt", etcCount)
        ));

        //차종 그룹
        map.put("carTypeGroups", groups);

        //  지역(시/도) 통합 전처리
        List<ImportCarSaleRepository.FacetAgg> rawLoc = repo.countBySaleLocation();

        Map<String, Long> locMap = new LinkedHashMap<>();

        for (var r : rawLoc) {
            String rawName = r.getVal();

            // 1) 광역시/도만 추출하는 정규식 패턴
            String upper = rawName
                    .replaceAll("^(서울특별시|서울).*", "서울")
                    .replaceAll("^(경기도|경기).*", "경기")
                    .replaceAll("^(부산광역시|부산).*", "부산")
                    .replaceAll("^(대구광역시|대구).*", "대구")
                    .replaceAll("^(인천광역시|인천).*", "인천")
                    .replaceAll("^(광주광역시|광주).*", "광주")
                    .replaceAll("^(대전광역시|대전).*", "대전")
                    .replaceAll("^(울산광역시|울산).*", "울산")
                    .replaceAll("^(세종특별자치시|세종).*", "세종")
                    .replaceAll("^(강원특별자치도|강원도|강원).*", "강원")
                    .replaceAll("^(충청북도|충북).*", "충북")
                    .replaceAll("^(충청남도|충남).*", "충남")
                    .replaceAll("^(전라북도|전북).*", "전북")
                    .replaceAll("^(전라남도|전남).*", "전남")
                    .replaceAll("^(경상북도|경북).*", "경북")
                    .replaceAll("^(경상남도|경남).*", "경남")
                    .trim();

            // 2) 같은 지역끼리 카운트 누적
            locMap.put(upper, locMap.getOrDefault(upper, 0L) + r.getCnt());
        }
        // 정렬
        List<Map.Entry<String, Long>> sorted = locMap.entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .toList();

        // Top 5
        List<Map.Entry<String, Long>> top5 = sorted.stream()
                .limit(5)
                .toList();

        // 나머지
        List<Map.Entry<String, Long>> others = sorted.stream()
                .skip(5)
                .toList();

        map.put("regionTop", top5);
        map.put("regionOthers", others);


        // 최종 지역 맵 저장
        map.put("regionGroups", locMap);

        //필터링 목록
        map.put("manufacturers", repo.distinctManufacturers());
        map.put("modelNames", repo.distinctModelNames());
        map.put("carNames", repo.distinctCarNames());
        map.put("fuelTypes", repo.distinctFuelTypes());
        map.put("transmissions", repo.distinctTransmissions());
        map.put("sellerTypes", repo.distinctSellerTypes());
        map.put("saleMethods", repo.distinctSaleMethods());
        map.put("saleLocations", repo.distinctSaleLocations());
        map.put("exteriorColors", repo.distinctExteriorColors());
        map.put("interiorColors", repo.distinctInteriorColors());
        map.put("performanceOpen", repo.distinctPerformanceOpen());

        //카운트
        map.put("countByManufacturer", repo.countByManufacturer());
        map.put("countByModelName", repo.countByModelName());
        map.put("countByCarName", repo.countByCarName());
        map.put("countByFuelType", repo.countByFuelType());
        map.put("countByTransmission", repo.countByTransmission());
        map.put("countBySellerType", repo.countBySellerType());
        map.put("countBySaleMethod", repo.countBySaleMethod());
        map.put("countBySaleLocation", repo.countBySaleLocation());
        map.put("countByExteriorColor", repo.countByExteriorColor());
        map.put("countByInteriorColor", repo.countByInteriorColor());
        map.put("countByPerformanceOpen", repo.countByPerformanceOpen());

        map.put("capacityBuckets", repo.capacityBuckets()); //인승

        return map;
    }
    private Specification<ImportCarSale> getCapacitySpec(List<String> caps) {
        return (root, q, cb) -> {

            // 하나라도 조건 맞으면 OR
            var predicates = caps.stream().map(cap -> {
                int c = Integer.parseInt(cap);

                if (c == 2) {
                    return root.get("capacity").in(List.of(0, 1, 2));
                } else if (c == 10) {
                    return cb.greaterThanOrEqualTo(root.get("capacity"), 10);
                } else {
                    return cb.equal(root.get("capacity"), c);
                }
            }).toList();

            return cb.or(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
    private Specification<ImportCarSale> buildSpec(ImportFilterRequest req) {

        Specification<ImportCarSale> spec = Specification.where(null);

        if (req == null) return spec;

        if (req.getCarType() != null && !req.getCarType().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("carType").in(req.getCarType()));
        }
        if (req.getManufacturer() != null && !req.getManufacturer().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("manufacturer").in(req.getManufacturer()));
        }
        if (req.getModelName() != null && !req.getModelName().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("modelName").in(req.getModelName()));
        }
        if (req.getFuelType() != null && !req.getFuelType().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("fuelType").in(req.getFuelType()));
        }
        if (req.getSaleLocation() != null && !req.getSaleLocation().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("saleLocation").in(req.getSaleLocation()));
        }
        if (req.getPerformanceOpen() != null && !req.getPerformanceOpen().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("performanceOpen").in(req.getPerformanceOpen()));
        }
        if (req.getCarName() != null && !req.getCarName().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("carName").in(req.getCarName()));
        }
        if (req.getTransmission() != null && !req.getTransmission().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("transmission").in(req.getTransmission()));
        }
        if (req.getSellerType() != null && !req.getSellerType().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("sellerType").in(req.getSellerType()));
        }
        if (req.getSaleMethod() != null && !req.getSaleMethod().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("saleMethod").in(req.getSaleMethod()));
        }
        if (req.getExteriorColor() != null && !req.getExteriorColor().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("exteriorColor").in(req.getExteriorColor()));
        }
        if (req.getInteriorColor() != null && !req.getInteriorColor().isEmpty()) {
            spec = spec.and((root, q, cb) -> root.get("interiorColor").in(req.getInteriorColor()));
        }
        if (req.getCapacity() != null && !req.getCapacity().isEmpty()) {
            spec = spec.and(getCapacitySpec(req.getCapacity()));
        }

        // 가격
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

        // 연식 (year + month)
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

        // 주행거리
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

        return spec;
    }

    // ===========================
    // 5) 페이지+정렬까지 포함된 신규 메서드
    // ===========================
    public Page<ImportCarCardDto> searchWithFilters(
            ImportFilterRequest req,
            String sort,
            int page,
            int size
    ) {
        Specification<ImportCarSale> spec = buildSpec(req);

        Sort sortOption = switch (sort) {
            case "priceAsc"    -> Sort.by("price").ascending();
            case "priceDesc"   -> Sort.by("price").descending();
            case "mileageAsc"  -> Sort.by("mileage").ascending();
            case "mileageDesc" -> Sort.by("mileage").descending();
            case "yearDesc"    -> Sort.by("year").descending().and(Sort.by("month").descending());
            default            -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sortOption);

        return repo.findAll(spec, pageable)
                .map(ImportCarCardDto::new);
    }

}
