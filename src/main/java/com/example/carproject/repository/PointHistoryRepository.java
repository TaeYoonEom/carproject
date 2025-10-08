package com.example.carproject.repository;

import com.example.carproject.domain.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Integer> {

    // 최신 순 전체 이력
    List<PointHistory> findByMemberIdOrderByDateDesc(Integer memberId);

    // 사용가능 잔액 (만료되지 않은 포인트: 만료일 NULL 또는 오늘 이후)
    @Query("""
        SELECT COALESCE(SUM(p.earnedPoints), 0) - COALESCE(SUM(p.usedPoints), 0)
          FROM PointHistory p
         WHERE p.memberId = :memberId
           AND (p.expirationDate IS NULL OR p.expirationDate >= :today)
    """)
    Integer currentUsableBalance(Integer memberId, LocalDate today);

    // 전체 누적 잔액(만료 무시)
    @Query("""
        SELECT COALESCE(SUM(p.earnedPoints), 0) - COALESCE(SUM(p.usedPoints), 0)
          FROM PointHistory p
         WHERE p.memberId = :memberId
    """)
    Integer totalBalance(Integer memberId);

    // 7일 내 만료 예정 포인트(단순 합; 정책에 따라 세분화 가능)
    @Query("""
        SELECT COALESCE(SUM(p.earnedPoints) - SUM(p.usedPoints), 0)
          FROM PointHistory p
         WHERE p.memberId = :memberId
           AND p.expirationDate BETWEEN :today AND :limit
    """)
    Integer expiringWithin7Days(Integer memberId, LocalDate today, LocalDate limit);
}
