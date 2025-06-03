package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MypageController {

    private final MemberRepository memberRepository;

    public MypageController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Authentication authentication) {
        Member member;

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            // ✅ 일반 로그인 사용자 (User, CustomUserDetails 모두 대응)
            String loginId = userDetails.getUsername();
            member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        } else if (principal instanceof OAuth2User oauthUser) {
            // ✅ 카카오 로그인 사용자
            Object idAttr = oauthUser.getAttribute("id");
            String loginId = "kakao_" + String.valueOf(idAttr);

            member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new IllegalArgumentException("카카오 로그인 사용자를 찾을 수 없습니다."));

        } else {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }

        model.addAttribute("member", member);
        return "mypage";
    }
}
