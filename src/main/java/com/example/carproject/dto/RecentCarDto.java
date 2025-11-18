package com.example.carproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentCarDto {
    private Integer carId;
    private String carName;
    private Integer year;
    private Integer price;
    private String imageUrl;

    private Integer origin;   // 0=국산, 1=수입
    private Integer isCargo;  // 0=승용, 1=화물

    private LocalDateTime viewedAt;   // ⭐ 최근 본 날짜 추가
}

