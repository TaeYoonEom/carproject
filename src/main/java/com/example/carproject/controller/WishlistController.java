package com.example.carproject.controller;

import com.example.carproject.security.CustomUserDetails;
import com.example.carproject.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/wish")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/toggle")
    public ResponseEntity<Map<String,Object>> toggle(
            @RequestBody Map<String, Integer> body,
            @AuthenticationPrincipal CustomUserDetails principal,
            HttpSession session) {

        Integer carId = body.get("carId");
        if (carId == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "msg", "carId missing"));
        }

        Integer memberId = null;
        if (principal != null) memberId = principal.getId();

        if (memberId == null) {
            Object mid = session.getAttribute("memberId");
            if (mid == null) mid = session.getAttribute("userId");
            if (mid == null) mid = session.getAttribute("id");
            if (mid instanceof Integer i) memberId = i;
            else if (mid instanceof Long l) memberId = l.intValue();
        }

        if (memberId == null) {
            return ResponseEntity.status(401).body(Map.of("ok", false, "msg", "login"));
        }

        try {
            boolean liked = wishlistService.toggle(memberId, carId);
            return ResponseEntity.ok(Map.of("ok", true, "liked", liked));
        } catch (Exception e) {
            // 서버 로그로 원인 추적에 도움
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("ok", false, "msg", e.getClass().getSimpleName()+": "+e.getMessage()));
        }
    }
}
