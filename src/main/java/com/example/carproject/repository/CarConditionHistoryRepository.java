package com.example.carproject.repository;

import com.example.carproject.domain.CarConditionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarConditionHistoryRepository extends JpaRepository<CarConditionHistory, Integer> {
    Optional<CarConditionHistory> findByCarId(Integer carId);   // car_id 유니크이므로 단건
}
