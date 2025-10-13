package com.example.carproject.service;

import com.example.carproject.domain.CarSold;
import com.example.carproject.repository.CarSoldRepository;
import com.example.carproject.repository.AllCarSaleRepositorys2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarSoldService {

    private final CarSoldRepository carSoldRepo;
    private final AllCarSaleRepositorys2 allCarSaleRepo;

    @Transactional
    public void setStatus(int memberId, int carId, CarSold.Status status) {

        // ✅ 존재 여부만 확인
        boolean isOwner = allCarSaleRepo.existsByCarIdAndMemberId(carId, memberId);
        if (!isOwner) {
            throw new SecurityException("본인 차량만 상태 변경 가능");
        }

        var row = carSoldRepo.findByCarId(carId).orElseGet(CarSold::new);
        row.setCarId(carId);
        row.setMemberId(memberId);
        row.setStatus(status);
        carSoldRepo.save(row);
    }


    @Transactional(readOnly = true)
    public int count(int memberId, CarSold.Status status) {
        return carSoldRepo.countByMemberIdAndStatus(memberId, status);
    }

    @Transactional(readOnly = true)
    public List<Integer> carIdsByStatus(int memberId, CarSold.Status status) {
        return carSoldRepo.findByMemberIdAndStatusOrderByUpdatedAtDesc(memberId, status)
                .stream().map(CarSold::getCarId).toList();
    }
}
