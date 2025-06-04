package com.example.carproject.buy.controller;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.service.CarSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ImportController {

    private final CarSaleService carSaleService;

    // 생성자 주입
    public ImportController(CarSaleService carSaleService) {
        this.carSaleService = carSaleService;
    }

    // 차량 목록 전체 + 우대/일반 구분해서 전달
    @GetMapping("/import")
    public String showKoreanCars(Model model) {
        // 전체 차량 목록 가져오기
        List<CarSale> carList = carSaleService.getAllCars();

        model.addAttribute("carList", carList);

        return "buy/import_page";
    }
}
