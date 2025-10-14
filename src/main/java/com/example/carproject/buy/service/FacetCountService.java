package com.example.carproject.buy.service;

import com.example.carproject.buy.repository.CarSaleRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FacetCountService {

    private final CarSaleRepository repo;

    @Getter @AllArgsConstructor
    public static class FacetItem {
        private String value;
        private long count;
    }

    public Map<String, List<FacetItem>> loadAllFacetCounts() {
        Map<String, List<FacetItem>> map = new LinkedHashMap<>();
        map.put("manufacturers", toItems(repo.countByManufacturer()));
        map.put("modelNames",    toItems(repo.countByModelName()));
        map.put("carNames",      toItems(repo.countByCarName()));
        map.put("exteriorColors",toItems(repo.countByExteriorColor()));
        map.put("interiorColors",toItems(repo.countByInteriorColor()));
        map.put("seatColors",    toItems(repo.countBySeatColor()));
        map.put("carTypes",      toItems(repo.countByCarType()));
        map.put("fuelTypes",     toItems(repo.countByFuelType()));
        map.put("transmissions", toItems(repo.countByTransmission()));
        map.put("sellerTypes",   toItems(repo.countBySellerType()));
        map.put("saleMethods",   toItems(repo.countBySaleMethod()));
        map.put("saleLocations", toItems(repo.countBySaleLocation()));
        map.put("capacities",    toItems(repo.countByCapacity()));
        return map;
    }

    private List<FacetItem> toItems(List<CarSaleRepository.FacetAgg> list) {
        return list.stream()
                .filter(r -> r.getVal() != null && !r.getVal().isBlank())
                .map(r -> new FacetItem(r.getVal(), r.getCnt()))
                .toList();
    }
}
