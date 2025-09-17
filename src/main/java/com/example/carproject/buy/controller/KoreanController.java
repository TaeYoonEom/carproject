package com.example.carproject.buy.controller;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.dto.CarCardDto;
import com.example.carproject.buy.service.CarSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class KoreanController {

    private final CarSaleService carSaleService;

    // 생성자 주입
    public KoreanController(CarSaleService carSaleService) {
        this.carSaleService = carSaleService;
    }

    // 차량 목록 전체 + 우대/일반 구분해서 전달
    @GetMapping("/korean")
    public String showKoreanCars(Model model) {
        List<CarCardDto> carCardDtoList = carSaleService.getCarCardDtos(); // DTO 리스트

        long totalCount = carSaleService.getAllCount();

        model.addAttribute("carList", carCardDtoList);
        model.addAttribute("totalCount", totalCount);
        return "buy/korean_page";
    }

}
