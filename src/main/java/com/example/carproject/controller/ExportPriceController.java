package com.example.carproject.controller;

import com.example.carproject.dto.ExportPriceCarDto;
import com.example.carproject.service.ExportPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ExportPriceController {

    private final ExportPriceService exportPriceService;

    // AJAX 요청: 비슷한 차량 시세 검색
    @GetMapping("/export-price/search")
    public String exportSearch(@RequestParam Integer carId, Model model) {

        var result = exportPriceService.findSimilarCars(carId);

        model.addAttribute("cars", result.getCars());
        model.addAttribute("count", result.getCount());
        model.addAttribute("minPrice", result.getMinPrice());
        model.addAttribute("maxPrice", result.getMaxPrice());
        model.addAttribute("avgPrice", result.getAvgPrice());

        return "mypage/export-price-result :: resultTable";
    }

    @GetMapping("/export/filter/makers")
    public String exportPage(Model model) {
        Map<String, List<String>> makers = exportPriceService.findManufacturersGrouped();
        model.addAttribute("makerGroups", makers);
        return "mypage";
    }


    // 모델 목록
    @GetMapping("/export/filter/models")
    @ResponseBody
    public List<String> loadModels(@RequestParam String maker) {
        return exportPriceService.findModelsByMaker(maker);
    }

    // 연식 목록
    @GetMapping("/export/filter/years")
    @ResponseBody
    public List<Integer> loadYears(@RequestParam String maker,
                                   @RequestParam String model) {
        return exportPriceService.findYears(maker, model);
    }

    // 실제 시세 데이터 조회
    @GetMapping("/export/filter/search")
    public String searchExport(@RequestParam String maker,
                               @RequestParam String model,
                               @RequestParam(required=false) Integer year,
                               Model mv) {

        var list = exportPriceService.findCarsByFilter(maker, model, year);

        // count
        int count = list.size();

        // min
        int minPrice = (count > 0)
                ? list.stream().mapToInt(ExportPriceCarDto::getPrice).min().orElse(0)
                : 0;

        // max
        int maxPrice = (count > 0)
                ? list.stream().mapToInt(ExportPriceCarDto::getPrice).max().orElse(0)
                : 0;

        // avg
        int avgPrice = (count > 0)
                ? (int) list.stream().mapToInt(ExportPriceCarDto::getPrice).average().orElse(0)
                : 0;

        mv.addAttribute("cars", list);
        mv.addAttribute("count", count);
        mv.addAttribute("minPrice", minPrice);
        mv.addAttribute("maxPrice", maxPrice);
        mv.addAttribute("avgPrice", avgPrice);

        return "mypage/export-filter-result :: result";
    }


}
