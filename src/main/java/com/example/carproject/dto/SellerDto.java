package com.example.carproject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SellerDto {
    private String name;
    private String phone;
    private String store; // 상사명(개인이면 null 가능)
    private String email;
    private String address;  // 주소 공개일 때만 세팅
    private LocalDateTime joinedAt;
}
