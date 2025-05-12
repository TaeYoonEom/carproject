package com.example.carproject.repository;

import com.example.carproject.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;  // 🔴 이거 꼭 import!

public interface MemberRepository extends JpaRepository<Member, Integer> {

    boolean existsByLoginId(String loginId);

    // 🔽 로그인 처리용 메서드 추가
    Optional<Member> findByLoginId(String loginId);
}
