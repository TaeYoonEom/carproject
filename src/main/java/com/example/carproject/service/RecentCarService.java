package com.example.carproject.service;

import com.example.carproject.domain.CarPurchaseRecent;
import com.example.carproject.repository.CarPurchaseRecentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentCarService {

    private final CarPurchaseRecentRepository repo;

    @Transactional
    public void addRecent(Integer memberId, Integer carId) {

        if (memberId == null || carId == null) return;

        // 1) 기존 중복 제거
        repo.deleteDuplicate(memberId, carId);

        // 2) 신규 insert
        CarPurchaseRecent r = new CarPurchaseRecent();
        r.setMemberId(memberId);
        r.setCarId(carId);
        r.setViewedAt(LocalDateTime.now());
        repo.save(r);

        // 3) 🔥 최근 본 목록이 15개 초과하면 오래된 순으로 삭제
        int limit = 15;
        List<Integer> ids = repo.findIdsOrdered(memberId);

        if (ids.size() > limit) {
            List<Integer> toDelete = ids.subList(limit, ids.size());
            repo.deleteByIds(toDelete);
        }
    }


    @Transactional(readOnly = true)
    public List<CarPurchaseRecent> getRecentList(Integer memberId, int limit) {
        return repo.findRecent(memberId, limit);
    }
}
