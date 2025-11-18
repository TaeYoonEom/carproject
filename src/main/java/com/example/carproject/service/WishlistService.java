package com.example.carproject.service;

import com.example.carproject.domain.AllCarSale;
import com.example.carproject.domain.Wishlist;
import com.example.carproject.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.carproject.dto.RecentCarDto;
import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.domain.ImportCarSale;
import com.example.carproject.buy.domain.CargoSpecialSale;
import com.example.carproject.domain.CarImage;


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
                a.car_id AS carId,
        
                /* 🔥 차량 이름: 국산/수입/car_sale + cargo까지 통합 */
                COALESCE(cs.car_name,
                         ic.car_name,
                         CONCAT(cg.manufacturer, ' ', cg.model_name)) AS carName,
        
                /* 🔥 연식: cargo에는 year만 있음 */
                COALESCE(cs.year, ic.year, cg.year) AS year,
        
                /* 🔥 가격 */
                COALESCE(cs.price, ic.price, cg.price) AS price,
        
                /* 🔥 이미지 경로 */
                img.front_view_url AS imageUrl
        
            FROM car_purchase_wishlist w
            JOIN all_car_sale a ON a.car_id = w.car_id
        
            /* ======================
               1) 국산차(car_sale)
               ====================== */
            LEFT JOIN (
                SELECT cs1.*
                FROM car_sale cs1
                JOIN (
                    SELECT car_id, MAX(created_at) AS max_created
                    FROM car_sale
                    GROUP BY car_id
                ) t
                ON cs1.car_id = t.car_id AND cs1.created_at = t.max_created
            ) cs ON cs.car_id = a.car_id
        
            /* ======================
               2) 수입차(import_car_sale)
               ====================== */
            LEFT JOIN (
                SELECT ic1.*
                FROM import_car_sale ic1
                JOIN (
                    SELECT car_id, MAX(created_at) AS max_created
                    FROM import_car_sale
                    GROUP BY car_id
                )t
                ON ic1.car_id = t.car_id AND ic1.created_at = t.max_created
            ) ic ON ic.car_id = a.car_id
        
            /* ======================
               3) 화물/특장(cargo_special_sale)
               ====================== */
            LEFT JOIN (
                SELECT cg1.*
                FROM cargo_special_sale cg1
                JOIN (
                    SELECT car_id, MAX(created_at) AS max_created
                    FROM cargo_special_sale
                    GROUP BY car_id
                ) t
                ON cg1.car_id = t.car_id AND cg1.created_at = t.max_created
            ) cg ON cg.car_id = a.car_id
        
            /* ======================
               대표 이미지
               ====================== */
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

    public RecentCarDto toRecentDto(Object carEntity, CarImage image, AllCarSale all) {

        if (carEntity == null) return null;

        Integer carId = null;
        String carName = null;
        Integer year = null;
        Integer price = null;

        int origin = all.getOrigin();       // 0 or 1
        int isCargo = all.getIsCargo() != null ? all.getIsCargo() : 0;  // 0 or 1

        // 🔹 화물차 (isCargo = 1)
        if (isCargo == 1 && carEntity instanceof CargoSpecialSale c) {
            carId = c.getCarId();
            carName = c.getManufacturer() + " " + c.getModelName();
            year = c.getYear();
            price = c.getPrice();
        }

        // 🔹 국산차 (origin 0 & isCargo 0)
        else if (isCargo == 0 && origin == 0 && carEntity instanceof CarSale c) {
            carId = c.getCarId();
            carName = c.getCarName();
            year = c.getYear();
            price = c.getPrice();
        }

        // 🔹 수입차 (origin 1 & isCargo 0)
        else if (isCargo == 0 && origin == 1 && carEntity instanceof ImportCarSale c) {
            carId = c.getCarId();
            carName = c.getCarName();
            year = c.getYear();
            price = c.getPrice();
        }

        // 이미지 세팅
        String imgUrl = (image != null && image.getFrontViewUrl() != null)
                ? image.getFrontViewUrl()
                : "/img/common/noimage.png";

        return new RecentCarDto(
                carId,
                carName,
                year,
                price,
                imgUrl,
                origin,    // 0 or 1
                isCargo,
                null// 0 or 1
        );
    }

}
