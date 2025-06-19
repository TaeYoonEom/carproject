package com.example.carproject.buy.controller;

import com.example.carproject.importcar.domain.ImportCarSale;
import com.example.carproject.buy.service.ImportCarSaleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ImportController {

    private final ImportCarSaleService importCarSaleService;

    public ImportController(ImportCarSaleService importCarSaleService) {
        this.importCarSaleService = importCarSaleService;
    }

    @GetMapping("/import")
    public String showImportCars(Model model) {
        List<ImportCarSale> carList = importCarSaleService.getAllImportCars();
        model.addAttribute("carList", carList);
        return "buy/import_page";
    }

}
