package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.dto.CarCardDto;
import com.example.carproject.buy.dto.FilterRequest;
import com.example.carproject.buy.repository.CarSaleRepository;
import com.example.carproject.buy.spec.CarSaleSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KoreanFilterService {

    private final CarSaleRepository repo;
    private final FacetViewService facetViewService;
    private final CarSaleService carSaleService;

    // 좌측 필터용 distinct 값 로딩
    public Map<String, List<?>> loadFilterOptions() {
        Map<String, List<?>> map = new LinkedHashMap<>();
        map.put("manufacturer", repo.distinctManufacturers());
        map.put("modelName", repo.distinctModelNames());
        map.put("carName", repo.distinctCarNames());
        map.put("performanceOpen", repo.distinctPerformanceOpen());
        map.put("fuelTypes", repo.distinctFuelTypes());
        map.put("transmissions", repo.distinctTransmissions());
        map.put("sellerTypes", repo.distinctSellerTypes());
        map.put("saleMethods", repo.distinctSaleMethods());
        map.put("carTypes", repo.distinctCarTypes());
        map.put("saleLocations", repo.distinctSaleLocations());
        map.put("driveTypes", repo.distinctDriveTypes());
        map.put("exteriorColors", repo.distinctExteriorColors());
        map.put("interiorColors", repo.distinctInteriorColors());
        map.put("seatColors", repo.distinctSeatColors());
        return map;
    }
    public List<CarCardDto> searchWithTopMakers(FilterRequest req) {
        // ✅ 상위 7개 제조사 추출
        var topMap = facetViewService.manufacturerCountsWithOthers(7);
        var topSet = topMap.keySet().stream()
                .filter(k -> !"기타 제조사".equals(k))
                .collect(Collectors.toSet());

        // ✅ 수정된 Spec 호출
        var cars = repo.findAll(
                CarSaleSpecs.from(req, topSet),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return carSaleService.toCardDtos(cars);
    }
    //----- 필터 조건 검색
    //public List<CarSale> search(FilterRequest req) {
    //    return repo.findAll(CarSaleSpecs.from(req), Sort.by(Sort.Direction.DESC, "createdAt"));
    //}


}

