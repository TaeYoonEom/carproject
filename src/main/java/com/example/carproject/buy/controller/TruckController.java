package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.TruckCardDto;
import com.example.carproject.buy.dto.TruckFilterRequest;
import com.example.carproject.buy.service.TruckSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TruckController {

    private final TruckSaleService truckSaleService;

    @GetMapping("/truck")
    public String showTruckPage(Model model) {
        var cards = truckSaleService.getTruckCards(); // List<TruckCardDto>
        model.addAttribute("truckCards", truckSaleService.getTruckCards());
        model.addAttribute("totalCount", truckSaleService.getTruckCount());
        Map<String, Object> filters = truckSaleService.buildFilters(null, null);
        model.addAttribute("filters", filters);
        return "buy/truck_page"; // ✅ templates/buy/truck_page.html
    }

    @GetMapping("/truck/filters")
    @ResponseBody
    public Map<String, Object> loadFilters(
            @RequestParam(required = false) String maker,
            @RequestParam(required = false) String model
    ) {
        return truckSaleService.buildFilters(maker, model);
    }
    @PostMapping("/truck/filter")
    @ResponseBody
    public Map<String, Object> filterTrucks(@RequestBody TruckFilterRequest req) {

        List<TruckCardDto> cards = truckSaleService.filterTrucks(req);

        Map<String, Object> result = new HashMap<>();
        result.put("cards", cards);

        return result;
    }

}
