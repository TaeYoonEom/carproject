package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.repository.CarEntryDraftRepository;
import com.example.carproject.repository.WishMini;
import com.example.carproject.repository.AllCarSaleRepository2; // ★ 리포지토리 이름/패키지 맞춤
import com.example.carproject.service.WishlistService;
import com.example.carproject.service.WishlistService.WishCarDto;
import com.example.carproject.domain.CarSold;
import com.example.carproject.service.CarSoldService;

// 쿠폰/포인트
import com.example.carproject.service.CouponPointService;
import com.example.carproject.dto.CouponSummary;
import com.example.carproject.dto.CouponRow;
import com.example.carproject.dto.PointSummary;
import com.example.carproject.dto.PointRow;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MypageController {

    private final MemberRepository memberRepository;
    private final AllCarSaleRepository2 allCarSaleRepository2; // ★ 필드명/타입 일치
    private final WishlistService wishlistService;
    private final CarSoldService carSoldService;

    private final CarEntryDraftRepository carEntryDraftRepository;
    private final CouponPointService couponPointService;

    public MypageController(MemberRepository memberRepository,
                            AllCarSaleRepository2 allCarSaleRepository2, // ★ 주입 타입/이름 일치
                            WishlistService wishlistService,
                            CarEntryDraftRepository carEntryDraftRepository,
                            CouponPointService couponPointService,
                            CarSoldService carSoldService) {
        this.memberRepository = memberRepository;
        this.allCarSaleRepository2 = allCarSaleRepository2;
        this.wishlistService = wishlistService;
        this.carEntryDraftRepository = carEntryDraftRepository;
        this.couponPointService = couponPointService;
        this.carSoldService = carSoldService;
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Member member = resolveMember(authentication);
        Integer memberId = member.getMemberId();

        // 1) 찜 데이터
        List<WishMini> wishMini = allCarSaleRepository2.findWishAll(memberId);
        List<WishCarDto> wishlistCars = wishlistService.myWishlistCars(memberId);
        int wishCount = wishlistService.count(memberId);

        // 2) 판매대기(Draft) + car_detail 이동용 carId 매핑
        List<CarEntryDraft> sellWaiting = carEntryDraftRepository
                .findByMemberIdAndIsSubmittedTrueOrderByCreatedAtDesc(memberId);
        int saleWaiting = sellWaiting.size();

        List<SellDraftCardVm> sellCards = sellWaiting.stream()
                .map(d -> new SellDraftCardVm(
                        d.getId(),
                        allCarSaleRepository2.findCarIdByDraftId(d.getId()).orElse(null),
                        d.getFrontViewUrl(),
                        d.getModelName(),
                        d.getCarNumber(),
                        d.getManufactureDate(),
                        d.getMileage(),
                        d.getRegion()
                ))
                .collect(Collectors.toList());

        // 3) 판매 상태 (car_sold)
        int saleOn       = carSoldService.count(memberId, CarSold.Status.판매중);
        int saleDone     = carSoldService.count(memberId, CarSold.Status.판매완료);
        int saleWithdraw = carSoldService.count(memberId, CarSold.Status.철회);

        int totalSellCars = saleWaiting + saleOn + saleDone; // 철회 제외

        // 4) 모델 바인딩
        model.addAttribute("member", member);

        model.addAttribute("wishCount", wishCount);
        model.addAttribute("wishMini", wishMini);
        model.addAttribute("wishlistCars", wishlistCars);

        model.addAttribute("sellWaiting", sellWaiting);
        model.addAttribute("saleWaiting", saleWaiting);
        model.addAttribute("sellDrafts", sellWaiting);  // 기존 템플릿 호환
        model.addAttribute("sellCards", sellCards);     // car_detail 링크용

        model.addAttribute("saleOn", saleOn);
        model.addAttribute("saleDone", saleDone);
        model.addAttribute("saleWithdraw", saleWithdraw);
        model.addAttribute("totalSellCars", totalSellCars);

        CouponSummary  couponSummary = couponPointService.getCouponSummary(memberId);
        List<CouponRow>  couponValid   = couponPointService.getUsableCoupons(memberId);
        List<CouponRow>  couponExpired = couponPointService.getUsedOrExpiredCoupons(memberId);
        PointSummary     pointSummary  = couponPointService.getPointSummary(memberId);
        List<PointRow>   pointRows     = couponPointService.getPointHistory(memberId);

        model.addAttribute("couponSummary", couponSummary);
        model.addAttribute("couponValid", couponValid);
        model.addAttribute("couponExpired", couponExpired);
        model.addAttribute("pointSummary", pointSummary);
        model.addAttribute("pointRows", pointRows);

        return "mypage";
    }

    private Member resolveMember(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String loginId = userDetails.getUsername();
            return memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        } else if (principal instanceof OAuth2User oauthUser) {
            Object idAttr = oauthUser.getAttribute("id");
            String loginId = "kakao_" + String.valueOf(idAttr);
            return memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("카카오 로그인 사용자를 찾을 수 없습니다."));
        }
        throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }

    // 화면 전용 VM
    public static class SellDraftCardVm {
        private final Integer draftId;
        private final Integer carId; // null이면 폴백
        private final String  frontViewUrl;
        private final String  modelName;
        private final String  carNumber;
        private final LocalDate manufactureDate;
        private final Integer mileage;
        private final String  region;

        public SellDraftCardVm(Integer draftId, Integer carId, String frontViewUrl,
                               String modelName, String carNumber,
                               LocalDate manufactureDate, Integer mileage, String region) {
            this.draftId = draftId;
            this.carId = carId;
            this.frontViewUrl = frontViewUrl;
            this.modelName = modelName;
            this.carNumber = carNumber;
            this.manufactureDate = manufactureDate;
            this.mileage = mileage;
            this.region = region;
        }

        public Integer getDraftId() { return draftId; }
        public Integer getCarId() { return carId; }
        public String getFrontViewUrl() { return frontViewUrl; }
        public String getModelName() { return modelName; }
        public String getCarNumber() { return carNumber; }
        public LocalDate getManufactureDate() { return manufactureDate; }
        public Integer getMileage() { return mileage; }
        public String getRegion() { return region; }
    }
}
