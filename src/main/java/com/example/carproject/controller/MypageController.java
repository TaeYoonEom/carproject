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

    // ✅ 추가: 판매 등록 조회용
    private final CarEntryDraftRepository carEntryDraftRepository;

    // ✅ 추가: 쿠폰/포인트 서비스
    private final CouponPointService couponPointService;

    public MypageController(MemberRepository memberRepository,
                            AllCarSaleRepository allCarSaleRepository,
                            WishlistService wishlistService,
                            CarEntryDraftRepository carEntryDraftRepository,   // ✅ 주입
                            CouponPointService couponPointService) {
        this.memberRepository = memberRepository;
        this.allCarSaleRepository = allCarSaleRepository;
        this.wishlistService = wishlistService;
        this.carEntryDraftRepository = carEntryDraftRepository;               // ✅ 주입
        this.couponPointService = couponPointService;
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // 1) 회원 조회
        Member member = resolveMember(authentication);
        Integer memberId = member.getMemberId();

        // 2) 찜
        List<WishMini> wishMini = allCarSaleRepository.findWishAll(memberId);

        // 3) 찜 테이블
        List<WishCarDto> wishlistCars = wishlistService.myWishlistCars(memberId);
        int wishCount = wishlistService.count(memberId);

        // 4) ✅ 판매 등록 차량(팝업에서 '완료' 눌러 isSubmitted=true 된 차량들)
        List<CarEntryDraft> sellDrafts =
                carEntryDraftRepository.findByMemberIdAndIsSubmittedTrueOrderByCreatedAtDesc(memberId);

        // 간단 상태 카운트(현재는 제출된 걸 '판매대기'로 표시, 나머지는 0)
        int saleWaiting = sellDrafts.size();
        int saleOn = saleWaiting;  // ✅ 여기만 수정
        int saleDone = 0;
        int saleDeleted = 0;

        int totalSellCars = saleWaiting + saleOn + saleDone; // 삭제는 총합에서 제외
        model.addAttribute("totalSellCars", totalSellCars);

        // 5) ✅ 쿠폰/포인트
        CouponSummary couponSummary = couponPointService.getCouponSummary(memberId);
        List<CouponRow> couponValid   = couponPointService.getUsableCoupons(memberId);
        List<CouponRow> couponExpired = couponPointService.getUsedOrExpiredCoupons(memberId);

        PointSummary pointSummary = couponPointService.getPointSummary(memberId);
        List<PointRow> pointRows  = couponPointService.getPointHistory(memberId);

        // 6) 모델 주입
        model.addAttribute("member", member);
        model.addAttribute("wishCount", wishCount);
        model.addAttribute("wishMini", wishMini);
        model.addAttribute("wishlistCars", wishlistCars);

        // ✅ 판매 차량 바인딩
        model.addAttribute("sellDrafts", sellDrafts);   // 목록
        model.addAttribute("saleWaiting", saleWaiting);
        model.addAttribute("saleOn", saleOn);
        model.addAttribute("saleDone", saleDone);
        model.addAttribute("saleDeleted", saleDeleted);

        // ✅ 쿠폰/포인트 바인딩
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
