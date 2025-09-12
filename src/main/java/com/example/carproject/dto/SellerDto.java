package com.example.carproject.dto;

import lombok.Data;

@Data
public class SellerDto {
    private String name;
    private String phone;
    private String store; // 상사명(개인이면 null 가능)
}
