package com.example.carproject.controller;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;
import com.example.carproject.service.CarService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

// CarDetailController.java
@Controller
public class CarDetailController {

    private final CarService carService;

    public CarDetailController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars/{carId}")
    public String carDetail(@PathVariable Long carId,
                            @RequestParam(required = false, defaultValue = "0") Integer origin,
                            Model model) {

        CarDto car;
        if (origin == 1) {  // ✅ 수입차
            car = carService.getImportCarDetail(carId);
        } else {            // ✅ 국산차
            car = carService.getCarDetail(carId);
        }
        model.addAttribute("car", car);

        model.addAttribute("mainImage", carService.getMainImage(carId));
        model.addAttribute("images", carService.getAllImages(carId));

        model.addAttribute("options", carService.getOptions(carId));
        model.addAttribute("inspection", carService.getInspectionMap(carId));
        model.addAttribute("inspectionImage", carService.getInspectionImage(carId));
        model.addAttribute("insurance", carService.getInsurance(carId));
        model.addAttribute("comparables", carService.getComparables(car));
        model.addAttribute("seller", carService.getSeller(carId));

        // ✅ 인터페이스 메서드 사용
        model.addAttribute("categoryName", carService.getCategoryName(carId));
        model.addAttribute("categoryPath", carService.getCategoryPath(carId));

        return "car_detail";
    }
}
