package com.example.carproject.repository;

import com.example.carproject.domain.CarSold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarSoldRepository extends JpaRepository<CarSold, Integer> { // ✅ ID 타입 int → Integer

    Optional<CarSold> findByCarId(int carId);

    List<CarSold> findByMemberIdAndStatusOrderByUpdatedAtDesc(int memberId, CarSold.Status status);

    int countByMemberIdAndStatus(int memberId, CarSold.Status status);
}
