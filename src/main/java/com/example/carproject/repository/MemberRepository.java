package com.example.carproject.repository;

import com.example.carproject.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;  // 🔴 이거 꼭 import!

public interface MemberRepository extends JpaRepository<Member, Integer> {

    boolean existsByLoginId(String loginId);

    // 🔽 로그인 처리용 메서드 추가
    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByNameAndPhone(String name, String phone); // 아이디 찾기
    Optional<Member> findByNameAndLoginIdAndPhone(String name, String loginId, String phone); // 비밀번호 찾기
    // 📌 MemberRepository.java 에 추가하세요
    Optional<Member> findByEmail(String email);


}
