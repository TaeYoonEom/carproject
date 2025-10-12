package com.example.carproject.repository;

import com.example.carproject.domain.CarConditionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarConditionHistoryRepository extends JpaRepository<CarConditionHistory, Integer> {
}
