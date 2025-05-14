package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    private final MemberRepository memberRepository;

    @Autowired
    public HelloController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 🔽 메인 페이지 (로그인 사용자 정보를 모델에 포함)
    @GetMapping("/")
    public String mainPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            Member member = memberRepository.findByLoginId(userDetails.getUsername()).orElse(null);
            model.addAttribute("member", member);
        }
        return "main_page";
    }
}