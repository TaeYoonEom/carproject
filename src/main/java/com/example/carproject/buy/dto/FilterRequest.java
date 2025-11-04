package com.example.carproject.buy.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class FilterRequest {
    private List<String> carNames; // 차이름
    private List<String> manufacturers; //제조사
    private List<String> modelNames; //차모델
    private List<String> exteriorColors; //외부색
    private List<String> interiorColors; //내부색
    private List<String> seatColors; //의자색
    private List<String> carTypes;  //차종
    private List<String> fuelTypes; //연료
    private List<String> transmissions; //변속기
    private List<String> sellerTypes;  //판매자구분
    private List<String> saleMethods; //판매방식
    private List<String> saleLocations;  //지역
    private List<Integer> capacities; // 인승
    private List<String> performanceOpen; //성능


    // 숫자 범위
    private Integer priceMin;
    private Integer priceMax;   // 단위: 만원 단위면 프런트에서 변환
    private Integer yearFrom;
    private Integer yearTo;
    private Integer monthFrom;
    private Integer monthTo;
    private Integer mileageMin;
    private Integer mileageMax;
}
