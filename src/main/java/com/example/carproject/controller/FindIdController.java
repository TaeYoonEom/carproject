package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class FindIdController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/find-id")
    public String showFindIdPage() {
        return "find_id";
    }

    @PostMapping("/find-id")
    public String findId(@RequestParam String name,
                         @RequestParam String phone,
                         Model model) {

        Optional<Member> member = memberRepository.findByNameAndPhone(name, phone);

        if (member.isPresent()) {
            model.addAttribute("foundId", member.get().getLoginId());
        } else {
            model.addAttribute("error", "입력하신 정보와 일치하는 아이디가 없습니다.");
        }

        return "find_id";
    }
}
