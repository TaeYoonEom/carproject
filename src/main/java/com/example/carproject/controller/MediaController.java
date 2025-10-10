package com.example.carproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MediaController {

    @GetMapping("/media")
    public String mediaPage() {
        // src/main/resources/templates/media.html 을 렌더링
        return "media";
    }
}
