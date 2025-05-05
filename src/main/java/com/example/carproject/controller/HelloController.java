package com.example.carproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/main")
    public String main_page() {
        return "main_page"; // templates/main_page.html로 이동
    }
}