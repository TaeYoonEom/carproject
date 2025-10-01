package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.ElectricCarCardDto;
import com.example.carproject.buy.service.ElectricCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ElectricController {

    private final ElectricCarService service;

    @GetMapping("/electric")
    public String ecoList(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "24") int size,
                          @RequestParam(defaultValue = "price") String sort,
                          @RequestParam(defaultValue = "DESC") String dir,
                          Model model) {

        Page<ElectricCarCardDto> result =
                service.getEcoCars(page, size, sort, Sort.Direction.fromString(dir));

        model.addAttribute("carList", result.getContent());
        model.addAttribute("totalCount", result.getTotalElements());
        model.addAttribute("page", result);

        // ✅ wishSet은 GlobalWishModelAdvice가 자동 주입 -> 여기서 넣지 않음
        return "buy/electric_page";
    }
}
