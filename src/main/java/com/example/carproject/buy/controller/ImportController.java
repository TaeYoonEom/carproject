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
        List<ImportCarCardDto> carList = importCarSaleService.getCardDtos();
        long totalCount = importCarSaleService.getAllCount();

        model.addAttribute("carList", carList);
        model.addAttribute("totalCount", totalCount);

        // ✅ wishSet은 GlobalWishModelAdvice가 자동 주입 -> 여기서 넣지 않음
        return "buy/import_page";
    }
}
