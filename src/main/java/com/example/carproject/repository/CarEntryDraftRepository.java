package com.example.carproject.repository;

import com.example.carproject.domain.CarEntryDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;         // ✅ 여기가 핵심
import java.util.Optional;    // ✅ 이것도 같이 필요

public interface CarEntryDraftRepository extends JpaRepository<CarEntryDraft, Long> {

    Optional<CarEntryDraft> findByMemberIdAndCarNumber(Long memberId, String carNumber);

    Optional<CarEntryDraft> findByCarNumber(String carNumber);

    // ✅ 내 차량 판매(제출 완료) 목록 조회용
    List<CarEntryDraft> findByMemberIdAndIsSubmittedTrueOrderByCreatedAtDesc(Integer memberId);
}
