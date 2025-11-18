package com.example.carproject.controller;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;
import com.example.carproject.security.CustomUserDetails;
import com.example.carproject.service.CarService;
import com.example.carproject.service.RecentCarService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CarDetailController {

    private final CarService carService;
    private final RecentCarService recentCarService;   // 🔥 추가됨

    @GetMapping("/cars/{carId}")
    public String carDetail(@PathVariable Long carId,
                            @RequestParam(required = false, defaultValue = "0") Integer origin,
                            Authentication authentication,
                            Model model) {

        // ===============================
        // 🔥 로그인된 사용자 memberId 가져오기
        // ===============================
        Integer memberId = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails user) {
            memberId = user.getMember().getMemberId();
        }

        // ===============================
        // 🔥 최근 본 차량 기록 추가
        // ===============================
        if (memberId != null) {
            recentCarService.addRecent(memberId, carId.intValue());
        }

        // ===============================
        // 차량 상세 정보 로직
        // ===============================
        CarDto car;
        if (origin == 1) {  // 수입차
            car = carService.getImportCarDetail(carId);
        } else {            // 국산차
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

        model.addAttribute("categoryName", carService.getCategoryName(carId));
        model.addAttribute("categoryPath", carService.getCategoryPath(carId));

        return "car_detail";
    }
}
