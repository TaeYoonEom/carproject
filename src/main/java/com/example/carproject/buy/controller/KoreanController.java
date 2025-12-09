package com.example.carproject.buy.controller;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.dto.CarCardDto;
import com.example.carproject.buy.dto.FilterRequest;
import com.example.carproject.buy.repository.CarImageRepository;
import com.example.carproject.buy.repository.CarSaleRepository;
import com.example.carproject.buy.service.CarSaleService;
import com.example.carproject.buy.service.FacetCountService;
import com.example.carproject.buy.service.FacetViewService;
import com.example.carproject.buy.service.KoreanFilterService;
import com.example.carproject.domain.CarImage;
import com.example.carproject.security.CustomUserDetails;
import com.example.carproject.service.WishlistService;
import jakarta.servlet.http.HttpSession; // Spring Boot 3.x (jakarta)
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class KoreanController {

    private final CarSaleService carSaleService;
    private final WishlistService wishlistService;
    private final KoreanFilterService filterService;
    private final FacetCountService facetCountService;
    private final FacetViewService facetViewService;
    private final CarSaleRepository carSaleRepository;
    private final CarImageRepository carImageRepository;


    // 차량 목록 전체 + 우대/일반 구분 + 내 찜 목록 표시용 wishSet 주입
    @GetMapping("/korean")
    public String showKoreanCars(Model model,
                                 @AuthenticationPrincipal CustomUserDetails principal,
                                 HttpSession session,
                                 @RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam(value = "sort", defaultValue = "recent") String sort) {

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

        FilterRequest emptyReq = new FilterRequest();   // 필터 필드는 전부 null/빈 리스트로
        var carPage = carSaleService.searchWithFilter(emptyReq, page, size, sort);

        // 내가 찜한 carId 집합 (없으면 빈 Set)
        Set<Integer> wishSet = (memberId != null)
                ? wishlistService.myWishCarIds(memberId)
                : Collections.emptySet();

        Map<String, List<?>> filterOptions = filterService.loadFilterOptions();


        model.addAttribute("carList", carPage.getContent());          // 🔥 10개만
        model.addAttribute("page", carPage);                          // 페이지 정보
        model.addAttribute("totalCount", carPage.getTotalElements());
        model.addAttribute("wishSet", wishSet);
        model.addAttribute("page", carPage);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentSize", size);

        // GET /korean 내부
        model.addAttribute("carTypeCounts", facetViewService.carTypeCountsZero());
        model.addAttribute("manufacturerCounts", facetViewService.manufacturerCountsWithOthers(7));
        model.addAttribute("saleLocationCounts", facetViewService.saleLocationCounts());
        model.addAttribute("capacityBucketCounts", facetViewService.capacityBucketCounts());
        model.addAttribute("performanceOpenCounts", facetViewService.performanceOpenCountsZero());
        model.addAttribute("sellerTypeCounts", facetViewService.sellerTypeCountsZero());
        model.addAttribute("saleMethodCounts", facetViewService.saleMethodCountsZero());
        model.addAttribute("exteriorColorCounts", facetViewService.exteriorColorCountsZero());
        model.addAttribute("interiorColorCounts", facetViewService.interiorColorCountsZero());
        model.addAttribute("fuelTypeCounts",      facetViewService.fuelTypeCountsZero());
        model.addAttribute("transmissionCounts",  facetViewService.transmissionCountsZero());


        model.addAllAttributes(filterOptions);
        //필터링  개수
        Map<String, List<FacetCountService.FacetItem>> facetCounts = facetCountService.loadAllFacetCounts();
        model.addAllAttributes(facetCounts);

        return "buy/korean_page";
    }

    @GetMapping(value="/korean/facet/models", produces="text/html; charset=UTF-8")
    public String loadModelsFacet(@RequestParam("maker") String maker,
                                  @RequestParam(value = "modelName", required = false) String modelName,
                                  @RequestParam(value="top", defaultValue="6") int top,
                                  Model model) {

        FacetViewService.MakerFacet facet = facetViewService.buildMakerFacet(maker, modelName, top);
        model.addAttribute("facet", facet);
        return "buy/partials/_maker_models :: models";
    }

    @PostMapping(value="/korean/filter", produces="text/html; charset=UTF-8")
    public String applyFilter(@RequestBody FilterRequest req,
                              @RequestParam(value = "page", defaultValue = "1") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(value = "sort", defaultValue = "recent") String sort,
                              @RequestParam(value = "mode", defaultValue = "all") String mode,
                              Model model,
                              @AuthenticationPrincipal CustomUserDetails principal,
                              HttpSession session) {

        // ✅ 로그인 사용자 확인
        Integer memberId = null;
        if (principal != null) memberId = principal.getId();
        else {
            Object mid = session.getAttribute("memberId");
            if (mid instanceof Integer i) memberId = i;
            else if (mid instanceof Long l) memberId = l.intValue();
        }

        // ✅ 정렬 + 페이지 + 필터 적용
        var carPage = carSaleService.searchWithFilter(req, page, size, sort);

        // ✅ 찜 차량
        var wishSet = (memberId != null)
                ? wishlistService.myWishCarIds(memberId)
                : Collections.emptySet();

        // ✅ model 세팅
        model.addAttribute("carList", carPage.getContent());
        model.addAttribute("wishSet", wishSet);
        model.addAttribute("page", carPage);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentSize", size);

        if (mode.equals("general")) {
            return "buy/partials/_car_cards_general :: general-list";
        } else {
            return "buy/partials/_car_cards :: list";
        }

    }

    public String filterCars(@RequestBody Map<String, Object> filters, Model model) {

        String yearFrom = (String) filters.get("yearFrom");
        String yearTo = (String) filters.get("yearTo");
        String mileageFrom = (String) filters.get("mileageFrom");
        String mileageTo = (String) filters.get("mileageTo");
        String priceFrom = (String) filters.get("priceFrom");
        String priceTo = (String) filters.get("priceTo");

        // ✅ 1) 모든 차량 불러오기
        List<CarSale> cars = carSaleRepository.findAll();

        // ✅ 2) 필터 적용
        Stream<CarSale> stream = cars.stream();

        if (yearFrom != null && !yearFrom.isBlank()) {
            int minYear = Integer.parseInt(yearFrom);
            stream = stream.filter(c -> c.getYear() != null && c.getYear() >= minYear);
        }
        if (yearTo != null && !yearTo.isBlank()) {
            int maxYear = Integer.parseInt(yearTo);
            stream = stream.filter(c -> c.getYear() != null && c.getYear() <= maxYear);
        }

        if (mileageFrom != null && !mileageFrom.isBlank()) {
            int minM = Integer.parseInt(mileageFrom);
            stream = stream.filter(c -> c.getMileage() != null && c.getMileage() >= minM);
        }
        if (mileageTo != null && !mileageTo.isBlank()) {
            int maxM = Integer.parseInt(mileageTo);
            stream = stream.filter(c -> c.getMileage() != null && c.getMileage() <= maxM);
        }

        if (priceFrom != null && !priceFrom.isBlank()) {
            int minP = Integer.parseInt(priceFrom);
            stream = stream.filter(c -> c.getPrice() != null && c.getPrice() >= minP);
        }
        if (priceTo != null && !priceTo.isBlank()) {
            int maxP = Integer.parseInt(priceTo);
            stream = stream.filter(c -> c.getPrice() != null && c.getPrice() <= maxP);
        }

        // ✅ CarSale → CarCardDto (AllCarSale 경유하여 이미지 가져오기)
        List<CarCardDto> carDtos = stream
                .map(CarCardDto::new)
                .toList();

        // ✅ 4) 모델에 DTO 리스트 추가
        model.addAttribute("carList", carDtos);

        return "buy/partials/_car_cards :: list";
    }

}
