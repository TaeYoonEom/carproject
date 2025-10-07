package com.example.carproject.buy.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class FilterRequest {
    // ✅ 프론트에서 넘어오는 JSON 구조 {"carTypes":["SUV","MD"]}
    private List<String> carTypes;
}
