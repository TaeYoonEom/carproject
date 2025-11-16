package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.TruckCardDto;
import com.example.carproject.buy.service.TruckSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TruckController {

    private final TruckSaleService truckService;

    @GetMapping("/truck")
    public String showTruckPage(Model model) {
        var cards = truckService.getTruckCards(); // List<TruckCardDto>
        model.addAttribute("truckCards", truckService.getTruckCards());
        model.addAttribute("totalCount", truckService.getTruckCount());
        //model.addAttribute("facet", truckService.getFacetData());
        return "buy/truck_page"; // ✅ templates/buy/truck_page.html
    }

    /*@PostMapping("/truck/filter")
    @ResponseBody
    public List<TruckCardDto> filter(@RequestBody FiltersRequest req) {
        return truckService.filter(req)
                .stream()
                .map(TruckCardDto::new)
                .toList();
    }*/

}
