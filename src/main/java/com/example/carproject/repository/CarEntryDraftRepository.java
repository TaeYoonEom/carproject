package com.example.carproject.repository;

import com.example.carproject.domain.CarEntryDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarEntryDraftRepository extends JpaRepository<CarEntryDraft, Long> {
    Optional<CarEntryDraft> findByMemberIdAndCarNumber(Long memberId, String carNumber);
}
