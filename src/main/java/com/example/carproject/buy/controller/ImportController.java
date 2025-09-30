package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.ImportCarCardDto;
import com.example.carproject.buy.service.ImportCarSaleService;
import com.example.carproject.service.WishlistService;
import jakarta.servlet.http.HttpSession; // Boot 3.x. (2.x면 javax.servlet)
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ImportController {

    private final ImportCarSaleService importCarSaleService;
    private final WishlistService wishlistService;

    public ImportController(ImportCarSaleService importCarSaleService,
                            WishlistService wishlistService) {
        this.importCarSaleService = importCarSaleService;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/import")
    public String showImportCars(Model model, HttpSession session) {
        List<ImportCarCardDto> carList = importCarSaleService.getCardDtos();
        long totalCount = importCarSaleService.getAllCount();

        // 세션의 memberId 안전 추출 (Integer/Long 모두 대응)
        Integer memberId = null;
        Object mid = session.getAttribute("memberId");
        if (mid instanceof Integer) {
            memberId = (Integer) mid;
        } else if (mid instanceof Long) {
            memberId = ((Long) mid).intValue();
        }

        // 찜해둔 carId 집합 (Integer 셋으로 변환)
        Set<Integer> wishSet = (memberId != null)
                ? wishlistService.myWishCarIds(memberId).stream()
                .map(Integer::valueOf)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        model.addAttribute("carList", carList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("wishSet", wishSet);

        return "buy/import_page";
    }
}
