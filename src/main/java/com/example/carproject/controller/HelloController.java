package com.example.carproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/")
    public String main_page() {
        return "main_page";
    }

    @GetMapping("/korean")
    public String koreanCars_Page(){
        return "korean_cars";
    }
}