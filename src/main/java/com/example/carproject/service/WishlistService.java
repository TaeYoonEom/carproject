package com.example.carproject.service;

import com.example.carproject.domain.Wishlist;
import com.example.carproject.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository repo;

    @Transactional
    public boolean toggle(Integer memberId, Integer carId) {
        if (memberId == null || carId == null) {
            throw new IllegalArgumentException("memberId/carId is null");
        }
        return repo.findByMemberIdAndCarId(memberId, carId)
                .map(w -> { repo.delete(w); return false; })   // 있었으면 해제
                .orElseGet(() -> {                            // 없으면 추가
                    Wishlist w = new Wishlist();
                    w.setMemberId(memberId);
                    w.setCarId(carId);
                    repo.save(w);
                    return true;
                });
    }

    @Transactional(readOnly = true)
    public Set<Integer> myWishCarIds(Integer memberId) {
        if (memberId == null) return Collections.emptySet();
        return new HashSet<>(repo.findCarIdsByMemberId(memberId));
    }
}

