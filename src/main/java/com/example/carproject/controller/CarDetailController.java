package com.example.carproject.controller;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;
import com.example.carproject.repository.AllCarSaleRepository2;
import com.example.carproject.security.CustomUserDetails;
import com.example.carproject.service.CarService;
import com.example.carproject.service.ExportPriceService;
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
    private final AllCarSaleRepository2 allCarSaleRepository2;
    private final ExportPriceService exportPriceService;


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

// 🔥 1) all_car_sale 에서 cargo 여부 판단
        boolean isCargo = allCarSaleRepository2.findByCarId(carId.intValue())
                .map(all -> all.getIsCargo() != null && all.getIsCargo() == 1)
                .orElse(false);


// 🔥 2) 화물차면 무조건 getTruckCarDetail()
        if (isCargo) {
            car = carService.getTruckCarDetail(carId);
        }
// 🔥 3) 수입차(origin=1)
        else if (origin != null && origin == 1) {
            car = carService.getImportCarDetail(carId);
        }
// 🔥 4) 국산차
        else {
            car = carService.getCarDetail(carId);
        }

        model.addAttribute("car", car);

        model.addAttribute("mainImage", carService.getMainImage(carId));
        model.addAttribute("images", carService.getAllImages(carId));

        if (car.isCargo()) {
            model.addAttribute("cargoOptions", car.getCargoOptions());
        } else {
            model.addAttribute("options", carService.getOptions(carId));
        }
        model.addAttribute("inspection", carService.getInspectionMap(carId));
        model.addAttribute("inspectionImage", carService.getInspectionImage(carId));
        model.addAttribute("insurance", carService.getInsurance(carId));
        model.addAttribute("comparables", carService.getComparables(car));
        model.addAttribute("seller", carService.getSeller(carId));

        model.addAttribute("categoryName", carService.getCategoryName(carId));
        model.addAttribute("categoryPath", carService.getCategoryPath(carId));
        model.addAttribute("exportPrice", exportPriceService.findSimilarCars(carId.intValue()));
        model.addAttribute("makerGroups", exportPriceService.findManufacturersGrouped());


        return "car_detail";
    }
}
