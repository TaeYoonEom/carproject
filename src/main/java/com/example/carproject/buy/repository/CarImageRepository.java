package com.example.carproject.buy.repository;

import com.example.carproject.domain.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, Integer> {
    Optional<CarImage> findFirstByCarId(Integer carId);
}
