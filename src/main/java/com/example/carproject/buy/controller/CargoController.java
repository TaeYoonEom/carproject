// src/main/java/com/example/carproject/buy/controller/CargoController.java
package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.CargoCardDto;
import com.example.carproject.buy.service.CargoSpecialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CargoController {

    private final CargoSpecialService cargoService;

    @GetMapping("/cargo")
    public String showCargo(Model model) {
        List<CargoCardDto> list = cargoService.getCargoCards();
        long totalCount = cargoService.getCargoCount();

        model.addAttribute("carList", list);
        model.addAttribute("totalCount", totalCount);
        return "buy/cargo_page"; // ← 당신의 템플릿 파일명에 맞게
    }
}
