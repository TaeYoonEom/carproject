package com.example.carproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MarketPriceController {

    // 페이지(두 경로 모두 허용)
    @GetMapping({"/price", "/market/price"})
    public String price(Model model) {
        model.addAttribute("activeMenu", "price");
        model.addAttribute("pageTitle", "시세");
        return "market/price";
    }

    // ===== 목데이터 =====
    private record CarItem(
            long id, String make, String model, String series, Integer year,
            Integer mileageKm, String region, String fuel, String color,
            Integer priceManwon, String title, String summary,
            String thumbnailUrl, List<String> badges
    ) {}
    private static List<CarItem> SAMPLE = List.of(
            new CarItem(1,"현대","그랜저","HG",2013, 97320,"서울","GASOLINE","은색",850,
                    "현대 그랜저 HG240 모던","13년식 · 97,320km · 서울","/img/car_image/car_g80.jpg",
                    List.of("단순수리없음","전손무","1인소유")),
            new CarItem(2,"현대","그랜저","HG",2013,143843,"대구","LPG","검정",639,
                    "현대 그랜저 HG LPI HG300 모던","13년식 · 143,843km · 대구","/img/car_image/car_g80.jpg",
                    List.of("사이드미러양호","엔진룸양호","타이어양호")),
            new CarItem(3,"현대","그랜저","HG",2013, 26161,"서울","GASOLINE","은색",777,
                    "현대 그랜저 HG240 모던","13년식 · 26,161km · 서울","/img/car_image/car_g80.jpg",
                    List.of("사이드미러양호","엔진룸양호","타이어양호")),
            new CarItem(4,"현대","그랜저","HG",2013,104205,"경기","GASOLINE","흰색",750,
                    "현대 그랜저 HG240 모던","13년식 · 104,205km · 경기","/img/car_image/car_g80.jpg",
                    List.of("단순수리없음","전손무","1인소유")),
            new CarItem(5,"현대","그랜저","HG",2013, 38625,"서울","GASOLINE","은색",1190,
                    "현대 그랜저 HG240 모던","13년식 · 38,625km · 서울","/img/car_image/car_g80.jpg",
                    List.of("단순수리없음","전손무","1인소유"))
    );

    // ===== 리스트 API =====
    @GetMapping("/market/api/prices")
    @ResponseBody
    public Map<String, Object> list(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Integer priceMin,
            @RequestParam(required = false) Integer priceMax,
            @RequestParam(required = false) Integer mileageMin,
            @RequestParam(required = false) Integer mileageMax,
            @RequestParam(required = false) List<String> fuel,
            @RequestParam(required = false, name="colors") List<String> colors,
            @RequestParam(defaultValue = "RECENT") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<CarItem> filtered = new ArrayList<>(SAMPLE);

        // 필터
        if (make != null && !make.isBlank())
            filtered = filtered.stream().filter(c -> make.equals(c.make())).collect(Collectors.toList());
        if (model != null && !model.isBlank())
            filtered = filtered.stream().filter(c -> c.model().contains(model)).collect(Collectors.toList());
        if (series != null && !series.isBlank())
            filtered = filtered.stream().filter(c -> series.equals(c.series())).collect(Collectors.toList());
        if (yearFrom != null) filtered = filtered.stream().filter(c -> c.year()!=null && c.year() >= yearFrom).collect(Collectors.toList());
        if (yearTo   != null) filtered = filtered.stream().filter(c -> c.year()!=null && c.year() <= yearTo).collect(Collectors.toList());
        if (priceMin != null) filtered = filtered.stream().filter(c -> c.priceManwon()!=null && c.priceManwon() >= priceMin).collect(Collectors.toList());
        if (priceMax != null) filtered = filtered.stream().filter(c -> c.priceManwon()!=null && c.priceManwon() <= priceMax).collect(Collectors.toList());
        if (mileageMin != null) filtered = filtered.stream().filter(c -> c.mileageKm()!=null && c.mileageKm() >= mileageMin).collect(Collectors.toList());
        if (mileageMax != null) filtered = filtered.stream().filter(c -> c.mileageKm()!=null && c.mileageKm() <= mileageMax).collect(Collectors.toList());
        if (fuel != null && !fuel.isEmpty())
            filtered = filtered.stream().filter(c -> fuel.contains(c.fuel())).collect(Collectors.toList());
        if (colors != null && !colors.isEmpty())
            filtered = filtered.stream().filter(c -> colors.contains(c.color())).collect(Collectors.toList());

        // 정렬
        Comparator<CarItem> cmp = Comparator.comparing(CarItem::id).reversed();
        if ("PRICE_ASC".equalsIgnoreCase(sort))   cmp = Comparator.comparing(CarItem::priceManwon, Comparator.nullsLast(Integer::compareTo));
        if ("PRICE_DESC".equalsIgnoreCase(sort))  cmp = Comparator.comparing(CarItem::priceManwon, Comparator.nullsLast(Integer::compareTo)).reversed();
        if ("MILEAGE_ASC".equalsIgnoreCase(sort)) cmp = Comparator.comparing(CarItem::mileageKm, Comparator.nullsLast(Integer::compareTo));
        filtered.sort(cmp);

        // 페이징
        int from = Math.min(page * size, filtered.size());
        int to   = Math.min(from + size, filtered.size());
        List<Map<String,Object>> content = filtered.subList(from, to).stream().map(c -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id", c.id());
            m.put("thumbnailUrl", c.thumbnailUrl());
            m.put("title", c.title());
            m.put("summary", c.summary());
            m.put("priceManwon", c.priceManwon());
            m.put("badges", c.badges());
            return m;
        }).toList();

        Map<String,Object> pageMap = new LinkedHashMap<>();
        pageMap.put("content", content);
        pageMap.put("totalElements", filtered.size());
        pageMap.put("totalPages", (int)Math.ceil(filtered.size()/(double)size));
        pageMap.put("number", page);
        return pageMap;
    }

    // ===== 통계 API =====
    @GetMapping("/market/api/prices/stats")
    @ResponseBody
    public Map<String, Object> stats(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String series,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Integer priceMin,
            @RequestParam(required = false) Integer priceMax,
            @RequestParam(required = false) Integer mileageMin,
            @RequestParam(required = false) Integer mileageMax,
            @RequestParam(required = false) List<String> fuel,
            @RequestParam(required = false, name="colors") List<String> colors
    ) {
        // 리스트 API와 동일한 필터 로직 재사용
        Map<String,Object> all = list(make, model, series, yearFrom, yearTo, priceMin, priceMax,
                mileageMin, mileageMax, fuel, colors, "RECENT", 0, Integer.MAX_VALUE);

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> content = (List<Map<String,Object>>) all.get("content");
        int min = content.stream().map(m -> (Integer)m.get("priceManwon")).min(Integer::compareTo).orElse(0);
        int max = content.stream().map(m -> (Integer)m.get("priceManwon")).max(Integer::compareTo).orElse(0);
        int avg = content.isEmpty() ? 0 :
                (int)Math.round(content.stream().map(m -> (Integer)m.get("priceManwon")).mapToInt(Integer::intValue).average().orElse(0));

        Map<String,Object> s = new LinkedHashMap<>();
        s.put("minManwon", min);
        s.put("maxManwon", max);
        s.put("avgManwon", avg);
        s.put("totalCount", content.size());
        return s;
    }
}
