package com.example.carproject.controller;

import com.example.carproject.domain.CarSold;
import com.example.carproject.service.CarSoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sell")
public class SellStatusController {

    private final CarSoldService carSoldService;

    @PostMapping("/status")
    public ResponseEntity<?> changeStatus(@RequestBody Map<String, String> body,
                                          Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        // ✅ memberId 추출 (CustomUserDetails 기반)
        var principal = authentication.getPrincipal();
        Integer memberId;
        if (principal instanceof com.example.carproject.security.CustomUserDetails userDetails) {
            memberId = userDetails.getMember().getMemberId();  // ← 여기가 핵심
        } else {
            return ResponseEntity.status(403).body("인증 정보를 확인할 수 없습니다.");
        }

        Integer carId = Integer.valueOf(body.get("carId"));
        CarSold.Status status = CarSold.Status.valueOf(body.get("status"));

        carSoldService.setStatus(memberId, carId, status);
        return ResponseEntity.ok().build();
    }
}
