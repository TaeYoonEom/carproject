package com.example.carproject.buy.service;

import com.example.carproject.buy.dto.ElectricCarCardDto;
import com.example.carproject.buy.projection.ElectricCarRow;
import com.example.carproject.buy.repository.ElectricCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectricCarService {

    private final ElectricCarRepository repo;

    public Page<ElectricCarCardDto> getEcoCars(int page, int size, String sort, Sort.Direction dir) {
        // 정렬 컬럼은 프로젝션 별칭과 같은 이름이어야 동작 (예: price, year 등)
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));
        Page<ElectricCarRow> rows = repo.findEcoCars(pageable);

        List<ElectricCarCardDto> items = rows.getContent().stream()
                .map(r -> new ElectricCarCardDto(
                        r.getCarId(), r.getOrigin(), r.getCarName(), r.getPrice(),
                        r.getYear(), r.getMileage(), r.getDriveType(),
                        r.getSaleLocation(), r.getOwnershipStatus(),
                        r.getImageUrl()
                ))
                .toList();

        return new PageImpl<>(items, pageable, rows.getTotalElements());
    }
}