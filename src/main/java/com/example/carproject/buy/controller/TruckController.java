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
    public String showTruckPage(
            @RequestParam(required = false) String bodyType,
            @RequestParam(required = false) String modelName,
            @RequestParam(required = false) Integer capacity,
            Model model
    ) {
        // 🔥 조건이 있으면 필터링된 리스트 반환
        List<TruckCardDto> cards = truckSaleService.filterByQuickSearch(bodyType, modelName, capacity);

        model.addAttribute("truckCards", cards);
        model.addAttribute("totalCount", cards.size());

        // 필터 옵션
        Map<String, Object> filters = truckSaleService.getFilters();
        model.addAttribute("filters", filters);

        return "buy/truck_page";
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

    @GetMapping("/api/truck/body-types")
    @ResponseBody
    public List<String> getBodyTypes() {
        return truckSaleService.getQuickBodyTypes();
    }

    @GetMapping("/api/truck/models")
    @ResponseBody
    public List<String> getModels(@RequestParam String bodyType) {
        return truckSaleService.getQuickModels(bodyType);
    }

    @GetMapping("/api/truck/capacity")
    @ResponseBody
    public List<Integer> getCapacity(@RequestParam String modelName) {
        return truckSaleService.getQuickCapacities(modelName);
    }


}
