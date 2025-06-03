package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.security.CustomUserDetails;
import com.example.carproject.security.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    private final MemberRepository memberRepository;

    @Autowired
    public MainController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 🔽 메인 페이지 - 로그인 사용자 정보 모델에 포함
    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal Object principal, Model model) {
        Member member = null;

        if (principal instanceof CustomUserDetails userDetails) {
            member = userDetails.getMember();
        } else if (principal instanceof CustomOAuth2User oAuth2User) {
            member = oAuth2User.getMember();
        }

        if (member != null) {
            model.addAttribute("member", member);
        }

        return "main_page";
    }
}
