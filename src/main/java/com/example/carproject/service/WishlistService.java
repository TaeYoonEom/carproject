package com.example.carproject.service;

import com.example.carproject.domain.Wishlist;
import com.example.carproject.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WishlistService {
    private final WishlistRepository repo;

    // ✅ 차량 상세를 함께 가져오려면 NamedParameterJdbcTemplate 주입 권장
    private final NamedParameterJdbcTemplate np;

    /**
     * 찜 토글: 있으면 해제(false), 없으면 추가(true)
     */
    @Transactional
    public boolean toggle(Integer memberId, Integer carId) {
        if (memberId == null || carId == null) {
            throw new IllegalArgumentException("memberId/carId is null");
        }
        return repo.findByMemberIdAndCarId(memberId, carId)
                .map(w -> { repo.delete(w); return false; })   // 있었으면 해제
                .orElseGet(() -> {                            // 없으면 추가
                    Wishlist w = new Wishlist();
                    w.setMemberId(memberId);
                    w.setCarId(carId);
                    repo.save(w);
                    return true;
                });
    }

    /**
     * 내가 찜한 car_id 집합
     */
    @Transactional(readOnly = true)
    public Set<Integer> myWishCarIds(Integer memberId) {
        if (memberId == null) return Collections.emptySet();
        return new HashSet<>(repo.findCarIdsByMemberId(memberId));
    }

    /**
     * ✅ 찜한 차량 상세 목록 (테이블 렌더링용)
     *  - all_car_sale + car_image(썸네일) 조인
     *  - 정책 반영 옵션(주석) : 판매완료/삭제/3개월 이전 찜은 제외
     */
    @Transactional(readOnly = true)
    public List<WishCarDto> myWishlistCars(Integer memberId) {
        if (memberId == null) return Collections.emptyList();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId);

        String sql = """
            SELECT
                a.car_id              AS carId,
                COALESCE(cs.car_name, ic.car_name)   AS carName,
                COALESCE(cs.year, ic.year)           AS year,
                COALESCE(cs.price, ic.price)         AS price,
                img.front_view_url    AS imageUrl
            FROM car_purchase_wishlist w
            JOIN all_car_sale a
              ON a.car_id = w.car_id
            LEFT JOIN (
                SELECT cs1.*
                FROM car_sale cs1
                JOIN (
                    SELECT car_id, MAX(created_at) AS max_created
                    FROM car_sale
                    GROUP BY car_id
                ) t ON t.car_id = cs1.car_id AND t.max_created = cs1.created_at
            ) cs ON cs.car_id = a.car_id
            LEFT JOIN (
                SELECT ic1.*
                FROM import_car_sale ic1
                JOIN (
                    SELECT car_id, MAX(created_at) AS max_created
                    FROM import_car_sale
                    GROUP BY car_id
                ) t ON t.car_id = ic1.car_id AND t.max_created = ic1.created_at
            ) ic ON ic.car_id = a.car_id
            LEFT JOIN car_image img
              ON img.car_id = a.car_id
             AND img.is_representative = 1
            WHERE w.member_id = :memberId
            ORDER BY w.created_at DESC, w.id DESC
            """;



        return np.query(sql, params, BeanPropertyRowMapper.newInstance(WishCarDto.class));
    }


    /**
     * ✅ 선택 삭제 (체크박스 일괄 삭제)
     */
    @Transactional
    public int removeSelected(Integer memberId, Collection<Integer> carIds) {
        if (memberId == null || carIds == null || carIds.isEmpty()) return 0;

        String sql = """
        DELETE FROM car_purchase_wishlist
         WHERE member_id = :memberId
           AND car_id IN (:ids)
        """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("ids", carIds);

        return np.update(sql, params);
    }

    @Transactional(readOnly = true)
    public int count(Integer memberId) {
        if (memberId == null) return 0;
        String sql = "SELECT COUNT(*) FROM car_purchase_wishlist WHERE member_id = :memberId";
        return Optional.ofNullable(
                np.queryForObject(sql, new MapSqlParameterSource("memberId", memberId), Integer.class)
        ).orElse(0);
    }


    /**
     * ✅ 테이블 렌더링용 DTO (필요시 필드 추가/수정)
     */
    public static class WishCarDto {
        private Integer carId;
        private String carName;
        private Integer year;
        private Integer price;
        private String imageUrl;

        // getter/setter
        public Integer getCarId() { return carId; }
        public void setCarId(Integer carId) { this.carId = carId; }
        public String getCarName() { return carName; }
        public void setCarName(String carName) { this.carName = carName; }
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public Integer getPrice() { return price; }
        public void setPrice(Integer price) { this.price = price; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
}
