package com.example.carproject.buy.service;

import com.example.carproject.buy.dto.ImportCarCardDto;
import com.example.carproject.buy.domain.ImportCarSale;
import com.example.carproject.buy.repository.ImportCarSaleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportCarSaleService {

    private final ImportCarSaleRepository repo;

    public ImportCarSaleService(ImportCarSaleRepository repo) {
        this.repo = repo;
    }

    public List<ImportCarCardDto> getCardDtos() {
        List<ImportCarSale> cars = repo.findAllWithImages(); // ⚡ N+1 방지
        return cars.stream()
                .map(ImportCarCardDto::new)
                .collect(java.util.stream.Collectors.toList()); // (JDK 8~15 호환)
        // .toList(); // JDK 16+면 이걸로 OK
    }

    public long getAllCount() {
        return repo.count();               // ✅ 필드명 맞추기
    }
}
