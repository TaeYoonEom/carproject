// MypageController.java
package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.repository.CarEntryDraftRepository;
import com.example.carproject.buy.repository.AllCarSaleRepository;
import com.example.carproject.buy.repository.WishMini;
import com.example.carproject.service.WishlistService;
import com.example.carproject.service.WishlistService.WishCarDto;
import com.example.carproject.domain.CarSold;
import com.example.carproject.service.CarSoldService;

// ✅ 쿠폰/포인트 서비스 & DTO
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

import java.util.List;

@Controller
public class MypageController {

    private final MemberRepository memberRepository;
    private final AllCarSaleRepository allCarSaleRepository;
    private final WishlistService wishlistService;
    private final CarSoldService carSoldService;

    // ✅ 추가: 판매 등록 조회용
    private final CarEntryDraftRepository carEntryDraftRepository;

    // ✅ 추가: 쿠폰/포인트 서비스
    private final CouponPointService couponPointService;

    public MypageController(MemberRepository memberRepository,
                            AllCarSaleRepository allCarSaleRepository,
                            WishlistService wishlistService,
                            CarEntryDraftRepository carEntryDraftRepository,
                            CouponPointService couponPointService,
                            CarSoldService carSoldService) {                 // ★ 추가
        this.memberRepository = memberRepository;
        this.allCarSaleRepository = allCarSaleRepository;
        this.wishlistService = wishlistService;
        this.carEntryDraftRepository = carEntryDraftRepository;
        this.couponPointService = couponPointService;
        this.carSoldService = carSoldService;                               // ★ 추가
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Member member = resolveMember(authentication);
        Integer memberId = member.getMemberId();

        // 찜
        var wishMini = allCarSaleRepository.findWishAll(memberId);
        var wishlistCars = wishlistService.myWishlistCars(memberId);
        int wishCount = wishlistService.count(memberId);

        // 판매대기: draft (is_submitted=1)
        var sellWaiting = carEntryDraftRepository
                .findByMemberIdAndIsSubmittedTrueOrderByCreatedAtDesc(memberId);
        int saleWaiting = sellWaiting.size();

        // 판매 상태: car_sold
        int saleOn       = carSoldService.count(memberId, CarSold.Status.판매중);
        int saleDone     = carSoldService.count(memberId, CarSold.Status.판매완료);
        int saleWithdraw = carSoldService.count(memberId, CarSold.Status.철회);

        int totalSellCars = saleWaiting + saleOn + saleDone; // 철회는 총합 제외 권장

        // 모델 주입
        model.addAttribute("member", member);
        model.addAttribute("wishCount", wishCount);
        model.addAttribute("wishMini", wishMini);
        model.addAttribute("wishlistCars", wishlistCars);

        model.addAttribute("sellWaiting", sellWaiting);
        model.addAttribute("saleWaiting", saleWaiting);
        model.addAttribute("sellDrafts", sellWaiting);
        model.addAttribute("saleOn", saleOn);
        model.addAttribute("saleDone", saleDone);
        model.addAttribute("saleWithdraw", saleWithdraw);
        model.addAttribute("totalSellCars", totalSellCars);

        // 쿠폰/포인트 (기존 그대로)
        var couponSummary = couponPointService.getCouponSummary(memberId);
        var couponValid   = couponPointService.getUsableCoupons(memberId);
        var couponExpired = couponPointService.getUsedOrExpiredCoupons(memberId);
        var pointSummary  = couponPointService.getPointSummary(memberId);
        var pointRows     = couponPointService.getPointHistory(memberId);

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
}
