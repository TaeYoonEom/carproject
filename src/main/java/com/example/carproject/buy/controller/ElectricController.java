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
import com.example.carproject.buy.service.ElectricResult;

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

        ElectricResult result = service.getEcoCars(page, size, sort, Sort.Direction.fromString(dir));

        model.addAttribute("carList", result.getPage().getContent());
        model.addAttribute("page", result.getPage());
        model.addAttribute("totalCount", result.getPage().getTotalElements());

        // ⭐ 좌측 필터 12종 Map
        model.addAttribute("filterCounts", result.getFilterCounts());

        return "buy/electric_page";
    }
}
