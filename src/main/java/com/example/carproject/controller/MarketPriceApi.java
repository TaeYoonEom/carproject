package com.example.carproject.controller;

import com.example.carproject.repository.MarketPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/market/api")
@RequiredArgsConstructor
public class MarketPriceApi {

    private final MarketPriceRepository repo;

    // 필터 맵 유틸
    private Map<String,Object> toFilter(
            String make, String model, Integer yearFrom, Integer yearTo,
            Integer priceMin, Integer priceMax, Integer mileageMin, Integer mileageMax,
            List<String> fuel, List<String> colors
    ){
        Map<String,Object> f = new HashMap<>();
        f.put("make", make); f.put("model", model);
        f.put("yearFrom", yearFrom); f.put("yearTo", yearTo);
        f.put("priceMin", priceMin); f.put("priceMax", priceMax);
        f.put("mileageMin", mileageMin); f.put("mileageMax", mileageMax);
        f.put("fuel", fuel); f.put("colors", colors);
        return f;
    }

    // ───── 드롭다운: 제조사(시세+개수) ─────
    // 프런트가 이미 /options/makes 를 호출하므로 여기서 시세까지 내려준다.
    @GetMapping("/options/makes")
    public List<Map<String,Object>> makesWithStats(
            @RequestParam(required=false) String make,
            @RequestParam(required=false) String model,
            @RequestParam(required=false) Integer yearFrom,
            @RequestParam(required=false) Integer yearTo,
            @RequestParam(required=false) Integer priceMin,
            @RequestParam(required=false) Integer priceMax,
            @RequestParam(required=false) Integer mileageMin,
            @RequestParam(required=false) Integer mileageMax,
            @RequestParam(required=false) List<String> fuel,
            @RequestParam(required=false, name="colors") List<String> colors
    ){
        return repo.makePriceStats(
                toFilter(make, model, yearFrom, yearTo, priceMin, priceMax, mileageMin, mileageMax, fuel, colors)
        ); // name, count, minManwon, maxManwon
    }

    // 필요하면 개수 전용 엔드포인트 유지
    @GetMapping("/options/makes/counts")
    public List<Map<String,Object>> makesCountsOnly(){
        return repo.makeCounts();
    }

    // ───── 드롭다운: 모델(시세+개수) ─────
    @GetMapping("/options/models")
    public List<Map<String,Object>> modelsWithStats(
            @RequestParam(required=false) String make,
            @RequestParam(required=false) String model,
            @RequestParam(required=false) Integer yearFrom,
            @RequestParam(required=false) Integer yearTo,
            @RequestParam(required=false) Integer priceMin,
            @RequestParam(required=false) Integer priceMax,
            @RequestParam(required=false) Integer mileageMin,
            @RequestParam(required=false) Integer mileageMax,
            @RequestParam(required=false) List<String> fuel,
            @RequestParam(required=false, name="colors") List<String> colors
    ){
        return repo.modelPriceStats(make,
                toFilter(make, model, yearFrom, yearTo, priceMin, priceMax, mileageMin, mileageMax, fuel, colors)
        ); // name, count, minManwon, maxManwon
    }

    @GetMapping("/options/models/counts")
    public List<Map<String,Object>> modelsCountsOnly(@RequestParam(required=false) String make){
        return repo.modelCounts(make);
    }

    // ───── 리스트 & 통계 기존 그대로 ─────
    @GetMapping("/prices")
    public Map<String,Object> prices(
            @RequestParam(required=false) String make,
            @RequestParam(required=false) String model,
            @RequestParam(required=false) Integer yearFrom,
            @RequestParam(required=false) Integer yearTo,
            @RequestParam(required=false) Integer priceMin,
            @RequestParam(required=false) Integer priceMax,
            @RequestParam(required=false) Integer mileageMin,
            @RequestParam(required=false) Integer mileageMax,
            @RequestParam(required=false) List<String> fuel,
            @RequestParam(required=false, name="colors") List<String> colors,
            @RequestParam(defaultValue="RECENT") String sort,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size
    ){
        Map<String,Object> f = toFilter(make, model, yearFrom, yearTo, priceMin, priceMax, mileageMin, mileageMax, fuel, colors);
        List<Map<String,Object>> content = repo.search(f, page, size, sort);
        int total = repo.count(f);

        Map<String,Object> pageMap = new LinkedHashMap<>();
        pageMap.put("content", content);
        pageMap.put("totalElements", total);
        pageMap.put("totalPages", (int)Math.ceil(total / (double)size));
        pageMap.put("number", page);
        return pageMap;
    }

    @GetMapping("/prices/stats")
    public Map<String,Object> stats(
            @RequestParam(required=false) String make,
            @RequestParam(required=false) String model,
            @RequestParam(required=false) Integer yearFrom,
            @RequestParam(required=false) Integer yearTo,
            @RequestParam(required=false) Integer priceMin,
            @RequestParam(required=false) Integer priceMax,
            @RequestParam(required=false) Integer mileageMin,
            @RequestParam(required=false) Integer mileageMax,
            @RequestParam(required=false) List<String> fuel,
            @RequestParam(required=false, name="colors") List<String> colors
    ){
        return repo.stats(
                toFilter(make, model, yearFrom, yearTo, priceMin, priceMax, mileageMin, mileageMax, fuel, colors)
        );
    }
}
