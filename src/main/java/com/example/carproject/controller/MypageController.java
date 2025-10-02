package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.buy.repository.AllCarSaleRepository;
import com.example.carproject.buy.repository.WishMini;
import com.example.carproject.service.WishlistService;
import com.example.carproject.service.WishlistService.WishCarDto;
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

    public MypageController(MemberRepository memberRepository,
                            AllCarSaleRepository allCarSaleRepository,
                            WishlistService wishlistService) {
        this.memberRepository = memberRepository;
        this.allCarSaleRepository = allCarSaleRepository;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // 1) 회원 조회
        Member member = resolveMember(authentication);

        // 2) 찜 미니 리스트(전체) - 3열 그리드, 9개 초과 시 뷰에서 내부 스크롤
        Integer memberId = member.getMemberId();
        List<WishMini> wishMini = allCarSaleRepository.findWishAll(memberId);

        // 3) 테이블 렌더링용 찜 목록 + 개수 (WishlistService 사용)
        List<WishCarDto> wishlistCars = wishlistService.myWishlistCars(memberId);
        int wishCount = wishlistService.count(memberId);

        // (옵션) 최근 본 차량도 필요하면 여기서 service 호출 후 model에 추가
        // List<RecentCarDto> recentCars = recentService.findRecent(memberId);
        // model.addAttribute("recentCars", recentCars);

        // 4) 모델 주입
        model.addAttribute("member", member);
        model.addAttribute("wishCount", wishCount);     // 상단 "총 N대" 표시용
        model.addAttribute("wishMini", wishMini);       // 미니 그리드
        model.addAttribute("wishlistCars", wishlistCars); // 테이블 섹션 바인딩

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
