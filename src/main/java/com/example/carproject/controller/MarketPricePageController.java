package com.example.carproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MarketPricePageController {

    // /price 와 /market/price 두 경로 모두 지원
    @GetMapping({"/price", "/market/price"})
    public String price(Model model) {
        model.addAttribute("activeMenu", "price");
        model.addAttribute("pageTitle", "시세");
        return "market/price"; // templates/market/price.html
    }
}
