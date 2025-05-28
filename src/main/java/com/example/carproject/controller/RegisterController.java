package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        return memberRepository.existsByLoginId(username);
    }


    @GetMapping("/step3")
    public String showStep3() {
        return "register/step3";
    }

    @PostMapping("/step3")
    public String register(@RequestParam String name,
                           @RequestParam String birth,
                           @RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String phone,
                           @RequestParam(required = false) String email,
                           @RequestParam String region1,
                           @RequestParam String region2) {

        if (memberRepository.existsByLoginId(username)) {
            return "redirect:/register/step3?error=duplicate";
        }

        Member member = new Member();
        member.setName(name);
        member.setLoginId(username);
        member.setPassword(passwordEncoder.encode(password));
        member.setPhone(phone);
        member.setEmail(email);
        member.setAddress(region1 + " " + region2);

        memberRepository.save(member);
        return "redirect:/register/step4";
    }
}
