package com.example.carproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExportPriceResult {

    private List<ExportPriceCarDto> cars;

    private int count;
    private int minPrice;
    private int maxPrice;
    private int avgPrice;
}
