package com.example.carproject.buy.service;

import com.example.carproject.buy.dto.CargoCardDto;
import com.example.carproject.buy.domain.CargoSpecialSale;

import java.util.List;

public interface CargoSpecialService {
    List<CargoSpecialSale> getAll();
    List<CargoCardDto> getCargoCards();
    long getCargoCount();
}
