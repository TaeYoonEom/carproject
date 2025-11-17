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

    // 제조사 목록 + 개수
    @GetMapping("/options/makes")
    public List<Map<String,Object>> makesWithCount(){
        return repo.makeCounts();   // [{name:"현대", count:103}, ...]
    }

    // 모델 목록 + 개수 (make 선택 시)
    @GetMapping("/options/models")
    public List<Map<String,Object>> modelsWithCount(@RequestParam(required=false) String make){
        return repo.modelCounts(make); // [{name:"G80", count:12}, ...]
    }

    // 리스트 (페이지네이션)
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
        Map<String,Object> f = new HashMap<>();
        f.put("make", make); f.put("model", model);
        f.put("yearFrom", yearFrom); f.put("yearTo", yearTo);
        f.put("priceMin", priceMin); f.put("priceMax", priceMax);
        f.put("mileageMin", mileageMin); f.put("mileageMax", mileageMax);
        f.put("fuel", fuel); f.put("colors", colors);

        List<Map<String,Object>> content = repo.search(f, page, size, sort);
        int total = repo.count(f);

        Map<String,Object> pageMap = new LinkedHashMap<>();
        pageMap.put("content", content);
        pageMap.put("totalElements", total);
        pageMap.put("totalPages", (int)Math.ceil(total / (double)size));
        pageMap.put("number", page);
        return pageMap;
    }

    // 통계
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
        Map<String,Object> f = new HashMap<>();
        f.put("make", make); f.put("model", model);
        f.put("yearFrom", yearFrom); f.put("yearTo", yearTo);
        f.put("priceMin", priceMin); f.put("priceMax", priceMax);
        f.put("mileageMin", mileageMin); f.put("mileageMax", mileageMax);
        f.put("fuel", fuel); f.put("colors", colors);
        return repo.stats(f);
    }
}
