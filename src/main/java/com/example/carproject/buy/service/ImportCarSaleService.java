package com.example.carproject.buy.service;

import com.example.carproject.importcar.domain.ImportCarSale;
import com.example.carproject.buy.repository.ImportCarSaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImportCarSaleService {

    private final ImportCarSaleRepository importCarSaleRepository;

    // 생성자 주입
    public ImportCarSaleService(ImportCarSaleRepository importCarSaleRepository) {
        this.importCarSaleRepository = importCarSaleRepository;
    }

    // ✅ 전체 수입 차량 목록 조회
    public List<ImportCarSale> getAllImportCars() {
        return importCarSaleRepository.findAll();
    }
}
