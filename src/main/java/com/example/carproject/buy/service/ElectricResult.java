package com.example.carproject.buy.service;

import com.example.carproject.buy.dto.ElectricCarCardDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ElectricResult {

    private final Page<ElectricCarCardDto> page;
    private final Map<String, Object> filterCounts;
}
