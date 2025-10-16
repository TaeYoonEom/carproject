package com.example.carproject.buy.service;

import com.example.carproject.buy.dto.CargoCardDto;

import java.util.List;

public interface CargoSpecialService {
    List<CargoCardDto> getCargoCards();
    long getCargoCount();
}
