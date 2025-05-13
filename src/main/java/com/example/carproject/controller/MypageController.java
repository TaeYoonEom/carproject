package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MypageController {

    private final MemberRepository memberRepository;

    public MypageController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Principal principal) {
        String loginId = principal.getName();  // Spring Security에서 로그인 ID 가져옴
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        model.addAttribute("member", member);
        return "mypage";
    }
}
