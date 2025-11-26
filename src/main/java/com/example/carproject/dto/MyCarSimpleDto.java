package com.example.carproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyCarSimpleDto {
    private Integer carId;
    private String modelName;
    private Integer year;
}
