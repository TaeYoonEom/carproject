package com.example.carproject.repository;

import com.example.carproject.domain.AllCarSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllCarSaleRepositorys2 extends JpaRepository<AllCarSale, Integer> {

    // 본인 차량 소유 여부 확인
    boolean existsByCarIdAndMemberId(int carId, int memberId);

    // ⬇️ 초안 기반 멱등 처리를 위해 추가
    Optional<AllCarSale> findByCarEntryDraftId(Integer draftId);
    boolean existsByCarEntryDraftId(Integer draftId);
}
