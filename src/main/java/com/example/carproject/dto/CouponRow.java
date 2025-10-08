package com.example.carproject.dto;

import java.time.LocalDate;

public record CouponRow(
        Integer id,
        String code,
        String name,
        Integer amount,     // discount_amount
        Float rate,         // discount_rate (float)
        LocalDate issuedDate,
        LocalDate expirationDate,
        boolean usable,
        long remainingDays  // today 기준 D-day (음수면 0)
) {}
