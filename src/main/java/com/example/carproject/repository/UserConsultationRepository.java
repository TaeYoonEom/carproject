package com.example.carproject.repository;

import com.example.carproject.domain.UserConsultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserConsultationRepository extends JpaRepository<UserConsultation, Integer> {

    List<UserConsultation> findByMemberIdOrderByCreatedAtDesc(Integer memberId);
}
