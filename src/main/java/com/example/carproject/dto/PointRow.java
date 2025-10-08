package com.example.carproject.dto;

import java.time.LocalDate;

public record PointRow(
        Integer id,
        String type,            // '적립' | '사용' | '만료'
        LocalDate date,         // DATE
        String description,     // TEXT
        LocalDate expirationDate,
        Integer earned,         // earned_points
        Integer used            // used_points
) {}
