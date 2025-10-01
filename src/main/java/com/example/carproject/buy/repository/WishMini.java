package com.example.carproject.buy.repository;

public interface WishMini {
    Integer getId();         // s.car_id
    String  getCarName();    // COALESCE(d.car_name, i.car_name)
    Long    getPrice();      // COALESCE(d.price, i.price)  (DB 타입에 맞춰 Integer/BigDecimal로 변경 가능)
    String  getFrontViewUrl();
}
