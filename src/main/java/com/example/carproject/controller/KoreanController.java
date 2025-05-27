package com.example.carproject.controller;

import com.example.carproject.domain.CarSale;
import com.example.carproject.service.CarSaleService;
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
//    @GetMapping("/korean") // 에러에러에러 주석처리
//    public String showKoreanCars(Model model) {
//        // 전체 차량 목록 가져오기
//        List<CarSale> carList = carSaleService.findByCarType("국산");
//
//        model.addAttribute("carList", carList);
//
//        return "korean_page";
//    }
}
