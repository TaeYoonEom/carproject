package com.example.carproject.dto;

public record CouponSummary(
        long ownedCount,     // 전체 보유(사용완료/만료 포함)
        long usableCount,    // 사용가능
        long expiring7days   // 7일 내 만료 예정(사용가능)
) {}
