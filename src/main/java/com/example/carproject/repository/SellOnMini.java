package com.example.carproject.repository;

public interface SellOnMini {
    Integer getCarId();
    String  getCarName();
    String  getCarNumber();
    Integer getYear();
    Integer getMileage();
    String  getSaleLocation();
    String  getFrontViewUrl();
    String  getStatus(); // 판매중 / 판매완료 등
    Integer getPrice();  // ✅ 추가
}
