package com.example.carproject.controller;

import com.example.carproject.service.SellSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sell")
public class SellSubmitController {

    private final SellSubmissionService service;

    /**
     * 내차등록 '제출'에서 호출 (draftId 전달)
     * 프런트: POST /api/sell/submit?draftId=6
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submit(
            @RequestParam Integer draftId,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        // 너의 SellStatusController와 동일 방식으로 memberId 추출
        Integer memberId;
        var principal = authentication.getPrincipal();
        if (principal instanceof com.example.carproject.security.CustomUserDetails user) {
            memberId = user.getMember().getMemberId();
        } else {
            return ResponseEntity.status(403).body("인증 정보를 확인할 수 없습니다.");
        }

        int carId = service.processDraftSubmission(draftId, memberId);
        return ResponseEntity.ok(Map.of("carId", carId, "status", "판매중"));
    }
}
