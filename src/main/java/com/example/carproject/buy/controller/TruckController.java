package com.example.carproject.buy.controller;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.service.CarSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TruckController {

    private final CarSaleService carSaleService;

    public TruckController(CarSaleService carSaleService) {
        this.carSaleService = carSaleService;
    }

    @GetMapping("/truck")
    public String showKoreanCars(Model model) {
        // 전체 차량 목록 가져오기
        List<CarSale> carList = carSaleService.getAllCars();

        model.addAttribute("carList", carList);

        return "buy/truck_page";
    }
}
