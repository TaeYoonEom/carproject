package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.CarCardDto;
import com.example.carproject.buy.service.CarSaleService;
import com.example.carproject.security.CustomUserDetails;
import com.example.carproject.service.WishlistService;
import jakarta.servlet.http.HttpSession; // Spring Boot 3.x (jakarta)
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class KoreanController {

    private final CarSaleService carSaleService;
    private final WishlistService wishlistService;

    // 차량 목록 전체 + 우대/일반 구분 + 내 찜 목록 표시용 wishSet 주입
    @GetMapping("/korean")
    public String showKoreanCars(Model model,
                                 @AuthenticationPrincipal CustomUserDetails principal,
                                 HttpSession session) {

        List<CarCardDto> carCardDtoList = carSaleService.getCarCardDtos();
        long totalCount = carSaleService.getAllCount();

        // 1) Security 로그인 사용자 ID
        Integer memberId = null;
        if (principal != null) {
            memberId = principal.getId(); // CustomUserDetails#getId() -> member.getMemberId()
        }

        // 2) 세션에서도 보조 확인 (키 이름이 다를 수 있으니 몇 개 체크)
        if (memberId == null) {
            Object mid = session.getAttribute("memberId");
            if (mid == null) mid = session.getAttribute("userId");
            if (mid == null) mid = session.getAttribute("id");

            if (mid instanceof Integer i) memberId = i;
            else if (mid instanceof Long l) memberId = l.intValue();
        }

        // 내가 찜한 carId 집합 (없으면 빈 Set)
        Set<Integer> wishSet = (memberId != null)
                ? wishlistService.myWishCarIds(memberId)
                : Collections.emptySet();

        model.addAttribute("carList", carCardDtoList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("wishSet", wishSet);

        return "buy/korean_page";
    }
}
