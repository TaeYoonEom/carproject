package com.example.carproject.service;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;

import java.util.List;
import java.util.Map;

public interface CarService {

    CarDto getCarDetail(Long carId);
    CarDto getImportCarDetail(Long carId);

    String getMainImage(Long carId);
    List<String> getAllImages(Long carId);

    List<String> getOptions(Long carId);
    Map<String, String> getInspectionMap(Long carId);
    String getInspectionImage(Long carId);
    InsuranceDto getInsurance(Long carId);
    List<ComparableCarDto> getComparables(CarDto car);

    SellerDto getSeller(Long carId);

    // ✅ 컨트롤러에서 instanceof 피하려고 인터페이스로 승격
    String getCategoryName(Long carId);
    String getCategoryPath(Long carId);
}
