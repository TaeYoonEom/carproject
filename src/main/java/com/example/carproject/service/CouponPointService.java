package com.example.carproject.service;

import com.example.carproject.domain.CouponHistory;
import com.example.carproject.domain.PointHistory;
import com.example.carproject.dto.CouponRow;
import com.example.carproject.dto.CouponSummary;
import com.example.carproject.dto.PointRow;
import com.example.carproject.dto.PointSummary;
import com.example.carproject.repository.CouponHistoryRepository;
import com.example.carproject.repository.PointHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CouponPointService {

    private final CouponHistoryRepository couponRepo;
    private final PointHistoryRepository pointRepo;

    public CouponPointService(CouponHistoryRepository couponRepo,
                              PointHistoryRepository pointRepo) {
        this.couponRepo = couponRepo;
        this.pointRepo = pointRepo;
    }

    // ===== 쿠폰 요약 =====
    public CouponSummary getCouponSummary(Integer memberId) {
        LocalDate today = LocalDate.now();
        long owned = couponRepo.findByMemberId(memberId).size();
        long usable = couponRepo.findUsable(memberId, today).size();
        long exp7  = couponRepo.countExpiringSoon(memberId, today, today.plusDays(7));
        return new CouponSummary(owned, usable, exp7);
    }

    // 사용가능 쿠폰
    public List<CouponRow> getUsableCoupons(Integer memberId) {
        LocalDate today = LocalDate.now();
        return couponRepo.findUsable(memberId, today).stream()
                .map(c -> mapCoupon(c, today))
                .toList();
    }

    // 사용완료/만료 쿠폰
    public List<CouponRow> getUsedOrExpiredCoupons(Integer memberId) {
        LocalDate today = LocalDate.now();
        return couponRepo.findUsedOrExpired(memberId, today).stream()
                .map(c -> mapCoupon(c, today))
                .toList();
    }

    private CouponRow mapCoupon(CouponHistory c, LocalDate today) {
        long dday = 0;
        if (c.getExpirationDate() != null) {
            long diff = ChronoUnit.DAYS.between(today, c.getExpirationDate());
            dday = Math.max(diff, 0);
        }
        return new CouponRow(
                c.getId(),
                c.getCouponCode(),
                c.getCouponName(),
                c.getDiscountAmount(),
                c.getDiscountRate(),
                c.getIssuedDate(),
                c.getExpirationDate(),
                Boolean.TRUE.equals(c.getIsUsable()),
                dday
        );
    }

    // ===== 포인트 요약/목록 =====
    public PointSummary getPointSummary(Integer memberId) {
        LocalDate today = LocalDate.now();
        int total  = nvl(pointRepo.totalBalance(memberId));
        int usable = nvl(pointRepo.currentUsableBalance(memberId, today));
        int exp7   = nvl(pointRepo.expiringWithin7Days(memberId, today, today.plusDays(7)));
        return new PointSummary(total, usable, exp7);
    }

    public List<PointRow> getPointHistory(Integer memberId) {
        return pointRepo.findByMemberIdOrderByDateDesc(memberId).stream()
                .map(this::mapPoint)
                .toList();
    }

    private PointRow mapPoint(PointHistory p) {
        return new PointRow(
                p.getId(),
                p.getType(),
                p.getDate(),
                p.getDescription(),
                p.getExpirationDate(),
                nvl(p.getEarnedPoints()),
                nvl(p.getUsedPoints())
        );
    }

    private int nvl(Integer v) { return v == null ? 0 : v; }
}
