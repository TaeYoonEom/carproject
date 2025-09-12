package com.example.carproject.dto;

import lombok.Data;

@Data
public class InsuranceDto {
    private Integer accidents;  // 사고/수리 건수
    private Integer totalLoss;  // 전손
    private Integer flood;      // 침수
    private Integer panels;     // 내/외판 교환 부위수
    private Integer cost;       // 보험사고 비용(원)
}
