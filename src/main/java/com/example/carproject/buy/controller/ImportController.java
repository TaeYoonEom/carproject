package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.ImportCarCardDto;
import com.example.carproject.buy.service.ImportCarSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ImportController {

    private final ImportCarSaleService importCarSaleService;

    public ImportController(ImportCarSaleService importCarSaleService) {
        this.importCarSaleService = importCarSaleService;
    }

    @GetMapping("/import")
    public String showImportCars(Model model) {

        // 1) 차량 카드
        List<ImportCarCardDto> carList = importCarSaleService.getCardDtos();

        // 2) 총 개수
        long totalCount = importCarSaleService.getAllCount();

        // 3) 🔥 필터 카운트 + distinct + 인승 버킷
        var filterCounts = importCarSaleService.getFilterCounts();

        // 4) 모델에 전달
        model.addAttribute("carList", carList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("filterCounts", filterCounts);

        // wishSet(찜 목록)은 GlobalWishModelAdvice에서 자동 추가됨
        return "buy/import_page";
    }
}
