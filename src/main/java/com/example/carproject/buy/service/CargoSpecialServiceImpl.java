package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.buy.dto.CargoCardDto;
import com.example.carproject.buy.repository.CargoSpecialSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoSpecialServiceImpl implements CargoSpecialService {

    private final CargoSpecialSaleRepository repo;

    @Override
    public List<CargoSpecialSale> getAll() {
        return repo.findAll();
    }

    @Override
    public List<CargoCardDto> getCargoCards() {
        return repo.findAll().stream()
                .map(CargoCardDto::new)
                .toList();
    }

    @Override
    public long getCargoCount() {
        return repo.count();
    }
}
