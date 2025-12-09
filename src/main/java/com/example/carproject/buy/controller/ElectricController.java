package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.ElectricCarCardDto;
import com.example.carproject.buy.dto.ElectricFilterRequest;
import com.example.carproject.buy.dto.ImportCarCardDto;
import com.example.carproject.buy.dto.ImportFilterRequest;
import com.example.carproject.buy.service.ElectricCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.carproject.buy.service.ElectricResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ElectricController {

    private final ElectricCarService electricCarService;

    @GetMapping("/electric")
    public String showElectricCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recent") String sort, // 기본 최근등록순
            @ModelAttribute ElectricFilterRequest filters,
            Model model) {

        // 1) 필터 + 정렬 + 페이지네이션 적용된 "일반등록" 리스트
        Page<ElectricCarCardDto> normalPage =
                electricCarService.searchWithFilters(filters, sort, page, size);

        // 2) 사진우대/우대등록용 리스트 (필터는 동일, 상위 8개만)
        List<ElectricCarCardDto> allFiltered =
                electricCarService.searchWithFilters(filters, sort, 0, 1000).getContent();

        List<ElectricCarCardDto> photoList   = allFiltered.stream().limit(8).toList();
        List<ElectricCarCardDto> premiumList = allFiltered.stream().limit(8).toList();

        // 필터 박스에 뿌릴 count 정보 (엔카식 facet)
        model.addAttribute("filterCounts", electricCarService.getFilterCounts(filters));

        // 화면에 뿌릴 데이터들
        model.addAttribute("filters", filters);                 // 선택값 유지용
        model.addAttribute("photoList", photoList);             // 사진우대
        model.addAttribute("premiumList", premiumList);         // 우대등록
        model.addAttribute("carList", normalPage.getContent()); // 일반등록

        model.addAttribute("page", normalPage);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);
        model.addAttribute("totalCount", normalPage.getTotalElements());

        return "buy/electric_page";
    }


}
