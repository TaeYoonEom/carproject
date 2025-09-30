package com.example.carproject.repository;

import com.example.carproject.domain.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    boolean existsByMemberIdAndCarId(Integer memberId, Integer carId);
    Optional<Wishlist> findByMemberIdAndCarId(Integer memberId, Integer carId);
    void deleteByMemberIdAndCarId(Integer memberId, Integer carId);

    @Query("select w.carId from Wishlist w where w.memberId = :memberId")
    List<Integer> findCarIdsByMemberId(@Param("memberId") Integer memberId);
}

