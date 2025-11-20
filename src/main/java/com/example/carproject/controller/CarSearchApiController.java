package com.example.carproject.controller;

import com.example.carproject.domain.AllCarSale;
import com.example.carproject.repository.AllCarSaleRepository2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class CarSearchApiController {

    private final AllCarSaleRepository2 allRepo;

    // 🔥 제조사 목록
    @GetMapping("/manufacturers")
    public List<String> manufacturers() {
        return allRepo.findAllManufacturers();
    }

    // 🔥 모델 목록
    @GetMapping("/models")
    public List<String> models(@RequestParam String manufacturer) {
        return allRepo.findModelsByManufacturer(manufacturer);
    }

    // 🔥 세부모델 목록
    @GetMapping("/detail-models")
    public List<String> detailModels(
            @RequestParam String manufacturer,
            @RequestParam String model) {

        return allRepo.findCarNames(manufacturer, model);
    }

    // 🔥 차량 1대 조회
    @GetMapping("/car")
    public Map<String, Object> searchCar(
            @RequestParam String manufacturer,
            @RequestParam String model,
            @RequestParam String carName) {

        Map<String, Object> res = new HashMap<>();

        Integer carId = allRepo.findCarIdForQuickSearch(manufacturer, model, carName)
                .orElse(null);

        res.put("carId", carId);
        res.put("count", carId != null ? 1 : 0);

        return res;
    }
}
