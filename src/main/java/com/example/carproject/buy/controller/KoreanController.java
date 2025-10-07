package com.example.carproject.buy.controller;

import com.example.carproject.buy.dto.CarCardDto;
import com.example.carproject.buy.dto.FilterRequest;
import com.example.carproject.buy.service.CarSaleService;
import com.example.carproject.buy.service.KoreanFilterService;
import com.example.carproject.security.CustomUserDetails;
import com.example.carproject.service.WishlistService;
import jakarta.servlet.http.HttpSession; // Spring Boot 3.x (jakarta)
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class KoreanController {

    private final CarSaleService carSaleService;
    private final WishlistService wishlistService;
    private final KoreanFilterService filterService;

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
        Map<String, Long> typeCounts = filterService.getTypeCountsByCode();

        model.addAttribute("carList", carCardDtoList);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("wishSet", wishSet);
        model.addAttribute("typeCounts", typeCounts);

        return "buy/korean_page";
    }
    // ✅ Ajax: 차종 필터 적용 → 카드 fragment만 반환 (text/html)
    @PostMapping(value = "/korean/filter", produces = "text/html")
    public String filterByCarType(@RequestBody FilterRequest req,
                                  Model model,
                                  @AuthenticationPrincipal CustomUserDetails principal,
                                  HttpSession session) {

        // 1) 조건 검색 (코드→한글 매핑은 Service에서 처리)
        var filteredCars = filterService.findByCodes(req.getCarTypes());

        // 2) DTO 변환 (서비스에 변환기가 없다면 carSaleService에 toCardDtos를 구현)
        List<CarCardDto> cardDtos = carSaleService.toCardDtos(filteredCars);

        // 3) 찜 표시 유지
        Integer memberId = null;
        if (principal != null) memberId = principal.getId();
        if (memberId == null) {
            Object mid = session.getAttribute("memberId");
            if (mid == null) mid = session.getAttribute("userId");
            if (mid == null) mid = session.getAttribute("id");
            if (mid instanceof Integer i) memberId = i;
            else if (mid instanceof Long l) memberId = l.intValue();
        }
        Set<Integer> wishSet = (memberId != null)
                ? wishlistService.myWishCarIds(memberId)
                : Collections.emptySet();

        model.addAttribute("carList", cardDtos);
        model.addAttribute("wishSet", wishSet);

        // ❗ buy/partials/_car_cards.html 에서 th:fragment="list" 로 카드영역 분리 필요
        return "buy/partials/_car_cards :: list";
    }
}
