package com.example.carproject.repository;

import com.example.carproject.domain.CouponHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CouponHistoryRepository extends JpaRepository<CouponHistory, Integer> {

    // 회원 전체 (보유/사용/만료 포함)
    List<CouponHistory> findByMemberId(Integer memberId);

    // 사용 가능(만료일 오늘 이상 + is_usable = 1)
    @Query("""
        SELECT c FROM CouponHistory c
         WHERE c.memberId = :memberId
           AND c.isUsable = true
           AND c.expirationDate >= :today
         ORDER BY c.expirationDate ASC, c.createdAt DESC
    """)
    List<CouponHistory> findUsable(Integer memberId, LocalDate today);

    // 사용완료 또는 만료(만료일 과거 OR is_usable=0)
    @Query("""
        SELECT c FROM CouponHistory c
         WHERE c.memberId = :memberId
           AND (c.isUsable = false OR c.expirationDate < :today)
         ORDER BY c.expirationDate DESC, c.createdAt DESC
    """)
    List<CouponHistory> findUsedOrExpired(Integer memberId, LocalDate today);

    // 7일 내 만료 예정(사용 가능 + 만료일이 오늘~7일)
    @Query("""
        SELECT COUNT(c) FROM CouponHistory c
         WHERE c.memberId = :memberId
           AND c.isUsable = true
           AND c.expirationDate BETWEEN :today AND :limit
    """)
    long countExpiringSoon(Integer memberId, LocalDate today, LocalDate limit);
}
