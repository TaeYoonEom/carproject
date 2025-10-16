package com.example.carproject.buy.controller;

import com.example.carproject.buy.service.CargoSpecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CargoController {

    private final CargoSpecialService cargoService;

    @GetMapping("/cargo")
    public String cargoPage(Model model) {
        model.addAttribute("cargoCards", cargoService.getCargoCards());
        model.addAttribute("totalCount",  cargoService.getCargoCount());
        return "buy/cargo_page"; // 템플릿 경로
    }
}
