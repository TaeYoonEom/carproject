package com.example.carproject.dto;

public record PointSummary(
        int total,              // 전체 누적(만료 무시)
        int usable,             // 사용가능(만료 제외)
        int expiring7days       // 7일 내 만료 예정 단순합
) {}
