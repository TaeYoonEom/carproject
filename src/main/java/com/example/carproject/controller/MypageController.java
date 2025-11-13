package com.example.carproject.controller;

import com.example.carproject.domain.*;
import com.example.carproject.dto.*;
import com.example.carproject.repository.*;
import com.example.carproject.repository.WishMini;
import com.example.carproject.repository.SellOnMini;
import com.example.carproject.service.*;
import com.example.carproject.service.WishlistService.WishCarDto;

// ✅ 쿠폰 / 포인트 관련

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MypageController {

    private final MemberRepository memberRepository;
    private final AllCarSaleRepository2 allCarSaleRepository2;
    private final WishlistService wishlistService;
    private final CarSoldService carSoldService;
    private final CarEntryDraftRepository carEntryDraftRepository;
    private final CouponPointService couponPointService;
    private final UserConsultationService userConsultationService;
    private final InquiryService inquiryService;

    public MypageController(MemberRepository memberRepository,
                            AllCarSaleRepository2 allCarSaleRepository2,
                            WishlistService wishlistService,
                            CarSoldService carSoldService,
                            CarEntryDraftRepository carEntryDraftRepository,
                            CouponPointService couponPointService,
                            UserConsultationService userConsultationService,
                            InquiryService inquiryService) {

        this.memberRepository = memberRepository;
        this.allCarSaleRepository2 = allCarSaleRepository2;
        this.wishlistService = wishlistService;
        this.carSoldService = carSoldService;
        this.carEntryDraftRepository = carEntryDraftRepository;
        this.couponPointService = couponPointService;
        this.userConsultationService = userConsultationService;
        this.inquiryService = inquiryService;
    }


    // ✅ 상태별 탭 필터링 (판매중 / 대기 / 완료 / 삭제)
    @GetMapping("/mypage")
    public String mypage(
            @RequestParam(defaultValue = "home") String section,
            @RequestParam(defaultValue = "판매중") String tab,
            Model model,
            Authentication authentication
    ) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        Member member = resolveMember(authentication);
        Integer memberId = member.getMemberId();

        /* ============================================================
           🔹 1) 찜한 차량
           ============================================================ */
        List<WishMini> wishMini = allCarSaleRepository2.findWishAll(memberId);
        List<WishCarDto> wishlistCars = wishlistService.myWishlistCars(memberId);
        int wishCount = wishlistService.count(memberId);

        /* ============================================================
           🔹 2) 판매대기 차량
           ============================================================ */
        List<CarEntryDraft> sellWaiting =
                carEntryDraftRepository.findByMemberIdAndIsSubmittedFalseOrderByCreatedAtDesc(memberId);

        List<SellDraftCardVm> sellCards = sellWaiting.stream()
                .map(d -> new SellDraftCardVm(
                        d.getId(),
                        allCarSaleRepository2.findCarIdByDraftId(d.getId()).orElse(null),
                        (d.getFrontViewUrl() != null && !d.getFrontViewUrl().isEmpty())
                                ? d.getFrontViewUrl()
                                : "/img/common/noimage.png",
                        d.getModelName(),
                        d.getCarNumber(),
                        d.getManufactureDate(),
                        d.getMileage(),
                        d.getRegion(),
                        d.getPrice()
                ))
                .collect(Collectors.toList());

        /* ============================================================
           🔹 3) 판매중 / 완료 / 삭제 차량
           ============================================================ */
        List<SellOnMini> soldRows = allCarSaleRepository2.findCarsByMemberAndStatus(memberId, tab);

        List<SellDraftCardVm> sellOnCards = soldRows.stream()
                .map(r -> new SellDraftCardVm(
                        null,
                        r.getCarId(),
                        (r.getFrontViewUrl() != null && !r.getFrontViewUrl().isEmpty())
                                ? r.getFrontViewUrl()
                                : "/img/common/noimage.png",
                        r.getCarName(),
                        r.getCarNumber(),
                        (r.getYear() != null ? LocalDate.of(r.getYear(), 1, 1) : null),
                        r.getMileage(),
                        r.getSaleLocation(),
                        r.getPrice()
                ))
                .toList();

        /* ============================================================
           🔹 카운트 계산
           ============================================================ */
        int saleWaiting = (int) sellCards.stream()
                .filter(d -> !allCarSaleRepository2.existsByCarEntryDraftId(d.getDraftId()))
                .count();

        int saleOn       = carSoldService.count(memberId, CarSold.Status.판매중);
        int saleDone     = carSoldService.count(memberId, CarSold.Status.판매완료);
        int saleWithdraw = carSoldService.count(memberId, CarSold.Status.철회);
        int saleDeleted  = carSoldService.count(memberId, CarSold.Status.삭제);

        int totalSellCars = saleOn + saleWaiting + saleDone;

        /* ============================================================
           🔹 탭 분기
           ============================================================ */
        if (tab.equals("판매대기")) {
            model.addAttribute("sellCards", sellCards);
            model.addAttribute("sellOnCards", List.of());
        } else {
            model.addAttribute("sellOnCards", sellOnCards);
            model.addAttribute("sellCards", List.of());
        }

        /* ============================================================
           🔹 공통 모델
           ============================================================ */
        model.addAttribute("member", member);
        model.addAttribute("wishCount", wishCount);
        model.addAttribute("wishMini", wishMini);
        model.addAttribute("wishlistCars", wishlistCars);

        model.addAttribute("saleOn", saleOn);
        model.addAttribute("saleWaiting", saleWaiting);
        model.addAttribute("saleDone", saleDone);
        model.addAttribute("saleWithdraw", saleWithdraw);
        model.addAttribute("saleDeleted", saleDeleted);
        model.addAttribute("totalSellCars", totalSellCars);

        model.addAttribute("currentTab", tab);
        model.addAttribute("currentSection", section);

        /* ============================================================
           🔹 쿠폰 & 포인트
           ============================================================ */
        model.addAttribute("couponSummary", couponPointService.getCouponSummary(memberId));
        model.addAttribute("couponValid", couponPointService.getUsableCoupons(memberId));
        model.addAttribute("couponExpired", couponPointService.getUsedOrExpiredCoupons(memberId));
        model.addAttribute("pointSummary", couponPointService.getPointSummary(memberId));
        model.addAttribute("pointRows", couponPointService.getPointHistory(memberId));

        /* ============================================================
           🔥 9) 나의 상담글 (user_consultation)
           ============================================================ */
        List<UserConsultation> myConsultations =
                userConsultationService.getMyConsultations(memberId);

        model.addAttribute("myConsultations", myConsultations);

        /* ============================================================
           🔥 10) 구매문의 (inquiry)
           ============================================================ */
        List<Inquiry> myInquiries =
                inquiryService.getMyInquiries(memberId);

        model.addAttribute("myInquiries", myInquiries);

        return "mypage";
    }



    // ✅ 로그인된 Member 조회
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

    // ✅ 화면 출력용 VM (판매대기 + 판매중 공용)
    public static class SellDraftCardVm {
        private final Integer draftId;
        private final Integer carId;
        private final String frontViewUrl;
        private final String modelName;
        private final String carNumber;
        private final LocalDate manufactureDate;
        private final Integer mileage;
        private final String region;
        private final Integer price;

        public SellDraftCardVm(Integer draftId, Integer carId, String frontViewUrl,
                               String modelName, String carNumber,
                               LocalDate manufactureDate, Integer mileage,
                               String region, Integer price) {
            this.draftId = draftId;
            this.carId = carId;
            this.frontViewUrl = frontViewUrl;
            this.modelName = modelName;
            this.carNumber = carNumber;
            this.manufactureDate = manufactureDate;
            this.mileage = mileage;
            this.region = region;
            this.price = price;

        }

        public Integer getDraftId() { return draftId; }
        public Integer getCarId() { return carId; }
        public String getFrontViewUrl() { return frontViewUrl; }
        public String getModelName() { return modelName; }
        public String getCarNumber() { return carNumber; }
        public LocalDate getManufactureDate() { return manufactureDate; }
        public Integer getMileage() { return mileage; }
        public String getRegion() { return region; }
        public String getSaleLocation() { return region; } // region 재활용
        public Integer getPrice() { return price; }
    }

    @PostMapping("/mypage/consultation/save")
    @ResponseBody
    public String saveConsultation(
            @RequestBody UserConsultationDto dto,
            Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return "ERROR";
        }

        Member member = resolveMember(authentication);

        userConsultationService.save(
                member.getMemberId(),
                dto.getId(),     // ⭐ 수정 버전
                dto.getTitle(),
                dto.getContent()
        );

        return "OK";
    }

    @DeleteMapping("/mypage/consultation/delete/{id}")
    @ResponseBody
    public String deleteConsultation(@PathVariable Integer id,
                                     Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return "ERROR";
        }

        userConsultationService.delete(id);
        return "OK";
    }


}
