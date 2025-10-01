package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.buy.repository.AllCarSaleRepository;
import com.example.carproject.buy.repository.WishMini;
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

    public MypageController(MemberRepository memberRepository,
                            AllCarSaleRepository allCarSaleRepository) {
        this.memberRepository = memberRepository;
        this.allCarSaleRepository = allCarSaleRepository;
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // 1) 회원 조회
        Member member;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            String loginId = userDetails.getUsername();
            member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        } else if (principal instanceof OAuth2User oauthUser) {
            Object idAttr = oauthUser.getAttribute("id");
            String loginId = "kakao_" + String.valueOf(idAttr);
            member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("카카오 로그인 사용자를 찾을 수 없습니다."));
        } else {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        // 2) 찜 미니 리스트(전체) - 3열 그리드, 9개 초과 시 뷰에서 내부 스크롤
        Integer memberId = member.getMemberId();
        List<WishMini> wishMini = allCarSaleRepository.findWishAll(memberId);
        int wishCount = wishMini.size();

        // 3) 모델 주입
        model.addAttribute("member", member);
        model.addAttribute("wishCount", wishCount);
        model.addAttribute("wishMini", wishMini);

        return "mypage";
    }
}
