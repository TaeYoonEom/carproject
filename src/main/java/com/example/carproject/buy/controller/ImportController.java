package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.ImportCarCardDto;
import com.example.carproject.buy.dto.ImportFilterRequest;
import com.example.carproject.buy.service.ImportCarSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ImportController {

    private final ImportCarSaleService importCarSaleService;

    // 🔥 GET 하나로 필터 + 정렬 + 페이지네이션 모두 처리
    @GetMapping("/import")
    public String showImportCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,       // 기본 10개씩
            @RequestParam(defaultValue = "recent") String sort, // 기본 최근등록순
            @ModelAttribute ImportFilterRequest filters,
            Model model) {

        // 1) 필터 + 정렬 + 페이지네이션 적용된 "일반등록" 리스트
        Page<ImportCarCardDto> normalPage =
                importCarSaleService.searchWithFilters(filters, sort, page, size);

        // 2) 사진우대/우대등록용 리스트 (필터는 동일, 상위 8개만)
        List<ImportCarCardDto> allFiltered =
                importCarSaleService.searchWithFilters(filters, sort, 0, 1000).getContent();

        List<ImportCarCardDto> photoList   = allFiltered.stream().limit(8).toList();
        List<ImportCarCardDto> premiumList = allFiltered.stream().limit(8).toList();

        // 필터 박스에 뿌릴 count 정보 (엔카식 facet)
        model.addAttribute("filterCounts", importCarSaleService.getFilterCounts());

        // 화면에 뿌릴 데이터들
        model.addAttribute("filters", filters);                 // 선택값 유지용
        model.addAttribute("photoList", photoList);             // 사진우대
        model.addAttribute("premiumList", premiumList);         // 우대등록
        model.addAttribute("carList", normalPage.getContent()); // 일반등록

        model.addAttribute("page", normalPage);
        model.addAttribute("sort", sort);
        model.addAttribute("size", size);
        model.addAttribute("totalCount", normalPage.getTotalElements());

        return "buy/import_page";
    }

}
