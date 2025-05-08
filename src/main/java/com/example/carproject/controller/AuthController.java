package com.example.carproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerRedirect() {
        return "redirect:/register/step1";
    }

    @GetMapping("/register/step1")
    public String step1() {
        return "register/step1";
    }

    @GetMapping("/register/step2")
    public String step2() {
        return "register/step2";
    }

    @GetMapping("/register/step3")
    public String step3() {
        return "register/step3";
    }

    @GetMapping("/register/step4")
    public String step4() {
        return "register/step4";
    }
}
