package com.example.carproject.repository;

import com.example.carproject.domain.CarEntryDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CarEntryDraftRepository extends JpaRepository<CarEntryDraft, Integer> {

    Optional<CarEntryDraft> findByMemberIdAndCarNumber(Integer memberId, String carNumber);

    Optional<CarEntryDraft> findByCarNumber(String carNumber);

    // ✅ 내 차량 판매(제출 완료) 목록 조회용
    List<CarEntryDraft> findByMemberIdAndIsSubmittedTrueOrderByCreatedAtDesc(Integer memberId);
    // ✅ 판매대기 (아직 제출되지 않은 차량)
    List<CarEntryDraft> findByMemberIdAndIsSubmittedFalseOrderByCreatedAtDesc(Integer memberId);

}
