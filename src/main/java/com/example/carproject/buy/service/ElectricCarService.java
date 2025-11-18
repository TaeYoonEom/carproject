package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.buy.dto.ElectricCarCardDto;
import com.example.carproject.buy.dto.ElectricFilterRequest;
import com.example.carproject.buy.projection.ElectricCarRow;
import com.example.carproject.buy.repository.ElectricCarRepository;
import com.example.carproject.buy.repository.ElectricFacetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ElectricCarService {

    private final ElectricCarRepository repo;
    private final ElectricFacetRepository facetRepo;

    private static final List<String> FIXED_BODY_TYPES = List.of(
            "경차","소형차","준중형차","중형차","대형차","스포츠카",
            "SUV","RV",
            "경승합차","승합차","화물차","기타"
    );

    private static final List<String> FIXED_ECO_TYPES = List.of(
            "하이브리드",
            "플러그인 하이브리드",
            "전기차",
            "LPG"
    );


    private static final List<String> FIXED_FUEL_TYPES = List.of(
            "LPG(일반인 구입)",
            "가솔린+전기",
            "디젤+전기",
            "LPG+전기",
            "전기",
            "수소"
    );

    //0으로 채움
    private List<Map<String,Object>> fillFixedList(List<String> fixed, List<Map<String,Object>> dbList) {
        Map<String, Long> countMap = new HashMap<>();

        // DB 값 맵핑
        for (Map<String,Object> row : dbList) {
            String key = (String) row.get("val");
            Long cnt = (Long) row.get("cnt");
            countMap.put(key, cnt);
        }

        // 고정 목록 + DB count 조합
        List<Map<String,Object>> result = new ArrayList<>();
        for (String item : fixed) {
            Map<String,Object> m = new HashMap<>();
            m.put("val", item);
            m.put("cnt", countMap.getOrDefault(item, 0L));
            result.add(m);
        }

        return result;
    }

    public Map<String, Object> buildFilterCounts() {

        Map<String, Object> map = new HashMap<>();

        List<Map<String,Object>> rawTypes = facetRepo.countCarType();

        /* ⭐ FIXED_BODY_TYPES(12개) 기반으로 고정 리스트 생성 */
        List<Map<String,Object>> fixedBodyList = fillFixedList(FIXED_BODY_TYPES, rawTypes);

        /* ⭐ FIXED 목록에 없는 값(예: 전기차, 초소형 등)은 기타로 합침 */
        long etcExtraCount = 0L;
        for (Map<String,Object> row : rawTypes) {
            String key = (String) row.get("val");

            if (!FIXED_BODY_TYPES.contains(key)) {
                etcExtraCount += (Long) row.get("cnt");
            }
        }

        /* 기타 카운트 증가 */
        Map<String,Object> etcMap = fixedBodyList.get(fixedBodyList.size() - 1); // 마지막 = “기타”
        long originEtc = (Long) etcMap.get("cnt");
        etcMap.put("cnt", originEtc + etcExtraCount);

        /* ⭐ 엔카식 섹션으로 나누기 */
        Map<String, List<Map<String,Object>>> bodySections = new LinkedHashMap<>();

        bodySections.put("승용", fixedBodyList.subList(0, 6));    // 경차~스포츠카
        bodySections.put("SUV/RV", fixedBodyList.subList(6, 8));  // SUV, RV
        bodySections.put("승합/화물", fixedBodyList.subList(8, 11)); // 경승합차~화물차
        bodySections.put("기타", fixedBodyList.subList(11, 12));  // 기타

        map.put("bodyTypes", bodySections);

        /* 전기·친환경차 종류 */
        List<Map<String,Object>> rawEco = facetRepo.countCarType();
        map.put("ecoTypes", fillFixedList(FIXED_ECO_TYPES, rawEco));

        /* 연료 */
        List<Map<String,Object>> rawFuel = facetRepo.countFuelType();
        map.put("fuelTypes", fillFixedList(FIXED_FUEL_TYPES, rawFuel));

        /* 지역 TOP / OTHERS */
        Map<String,Object> region = facetRepo.countRegion();
        map.put("regionTop",    region.get("top"));
        map.put("regionOthers", region.get("others"));

        /*  인승  */
        map.put("capacity", facetRepo.countCapacity());

        map.put("fuelType",      facetRepo.countFuelType());
        map.put("manufacturer",  facetRepo.countManufacturer());
        map.put("modelName",     facetRepo.countModelName());
        map.put("carName",       facetRepo.countCarName());
        map.put("region",        facetRepo.countRegion());
        map.put("exteriorColor", facetRepo.countExteriorColor());
        map.put("interiorColor", facetRepo.countInteriorColor());
        map.put("transmission",  facetRepo.countTransmission());
        map.put("sellerType",    facetRepo.countSellerType());
        map.put("saleMethod",    facetRepo.countSaleMethod());
        map.put("performance",   facetRepo.countPerformance());

        return map;
    }



    /** 차량목록 + 필터 전달 */
    public ElectricResult getEcoCars(int page, int size, String sort, Sort.Direction dir) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        Page<ElectricCarRow> rows = repo.findEcoCars(pageable);

        List<ElectricCarCardDto> list =
                rows.getContent().stream().map(ElectricCarCardDto::from).toList();

        return new ElectricResult(
                new PageImpl<>(list, pageable, rows.getTotalElements()),
                buildFilterCounts()
        );
    }
}