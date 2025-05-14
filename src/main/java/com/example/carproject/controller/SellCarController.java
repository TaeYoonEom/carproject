package com.example.carproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SellCarController {

    @GetMapping("/sell")
    public String showSellPage() {
        return "sell_cars"; // templates/sell_cars.html 렌더링
    }

    @GetMapping("/sell/quick")
    public String quickSell() {
        return "sell_quick"; // 빠른 판매용 별도 페이지
    }

    @GetMapping("/sell/direct")
    public String directSell() {
        return "sell_direct"; // 직접 판매용 별도 페이지
    }
}
