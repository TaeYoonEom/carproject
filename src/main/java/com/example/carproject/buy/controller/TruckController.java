package com.example.carproject.buy.controller;

import com.example.carproject.buy.service.TruckSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TruckController {

    private final TruckSaleService truckService;

    @GetMapping("/truck")
    public String showTruckPage(Model model) {
        var cards = truckService.getTruckCards(); // List<TruckCardDto>
        model.addAttribute("truckCards", truckService.getTruckCards());
        model.addAttribute("totalCount", truckService.getTruckCount());
        return "buy/truck_page"; // ✅ templates/buy/truck_page.html
    }
}
