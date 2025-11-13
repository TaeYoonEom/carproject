package com.example.carproject.repository;

import com.example.carproject.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {

    List<Inquiry> findByMemberIdOrderByCreatedAtDesc(Integer memberId);
}
