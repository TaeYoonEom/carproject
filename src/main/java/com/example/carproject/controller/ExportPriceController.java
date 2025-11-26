package com.example.carproject.controller;

import com.example.carproject.service.ExportPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

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
}
