package com.example.carproject.buy.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ElectricFacetRepository {

    private final EntityManager em;

    /* ----------------------------------------
       1) 공통 Facet Count (국산 + 수입 UNION ALL)
       ---------------------------------------- */
    private List<Map<String, Object>> runFacetQuery(String column) {

        String sql = """
            SELECT %s AS val, COUNT(*) AS cnt
            FROM (
                SELECT cs.%s
                FROM all_car_sale a
                JOIN car_sale cs
                  ON cs.all_car_sale_id = a.car_id OR cs.car_id = a.car_id
                WHERE a.is_eco_friendly = 1 AND cs.%s IS NOT NULL

                UNION ALL

                SELECT ims.%s
                FROM all_car_sale a
                JOIN import_car_sale ims
                  ON ims.all_car_sale_id = a.car_id OR ims.car_id = a.car_id
                WHERE a.is_eco_friendly = 1 AND ims.%s IS NOT NULL
            ) t
            GROUP BY %s
            ORDER BY cnt DESC
        """.formatted(column, column, column, column, column, column);

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        List<Map<String, Object>> list = new ArrayList<>();
        for (Object[] r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("val", r[0] != null ? r[0].toString() : "기타");
            m.put("cnt", ((Number) r[1]).longValue());
            list.add(m);
        }
        return list;
    }

    /* ----------------------------------------
       2) 개별 Facet (12종)
       ---------------------------------------- */
    public List<Map<String, Object>> countCarType() {
        return runFacetQuery("car_type");
    }

    public List<Map<String, Object>> countFuelType() {
        return runFacetQuery("fuel_type");
    }

    public List<Map<String, Object>> countManufacturer() {
        return runFacetQuery("manufacturer");
    }

    public List<Map<String, Object>> countModelName() {
        return runFacetQuery("model_name");
    }

    public List<Map<String, Object>> countCarName() {
        return runFacetQuery("car_name");
    }

    public List<Map<String, Object>> countExteriorColor() {
        return runFacetQuery("exterior_color");
    }

    public List<Map<String, Object>> countInteriorColor() {
        return runFacetQuery("interior_color");
    }

    public List<Map<String, Object>> countTransmission() {
        return runFacetQuery("transmission");
    }

    public List<Map<String, Object>> countSellerType() {
        return runFacetQuery("seller_type");
    }

    public List<Map<String, Object>> countSaleMethod() {
        return runFacetQuery("sale_method");
    }

    public List<Map<String, Object>> countPerformance() {
        return runFacetQuery("performance_open");
    }

    /* ----------------------------------------
       3) 지역 Region (TOP 5 + 나머지)
       ---------------------------------------- */
    public Map<String, Object> countRegion() {

        String sql = """
        SELECT sale_location, COUNT(*)
        FROM (
            SELECT cs.sale_location
            FROM all_car_sale a
            JOIN car_sale cs
              ON cs.all_car_sale_id = a.car_id OR cs.car_id = a.car_id
            WHERE a.is_eco_friendly = 1 AND cs.sale_location IS NOT NULL

            UNION ALL

            SELECT ims.sale_location
            FROM all_car_sale a
            JOIN import_car_sale ims
              ON ims.all_car_sale_id = a.car_id OR ims.car_id = a.car_id
            WHERE a.is_eco_friendly = 1 AND ims.sale_location IS NOT NULL
        ) t
    """;

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        /* ⭐ 1) 지역명 통일 & 카운트 누적 */
        Map<String, Long> locMap = new HashMap<>();

        for (Object[] r : rows) {
            String raw = r[0] != null ? r[0].toString().trim() : "";
            long cnt = ((Number) r[1]).longValue();

            if (raw.isBlank()) continue;

            // ⭐ 엔카식 정규화(너가 요청한 지역 통합 방식)
            String upper = raw
                    .replaceAll("^(서울특별시|서울).*", "서울")
                    .replaceAll("^(경기도|경기).*", "경기")
                    .replaceAll("^(부산광역시|부산).*", "부산")
                    .replaceAll("^(대구광역시|대구).*", "대구")
                    .replaceAll("^(인천광역시|인천).*", "인천")
                    .replaceAll("^(광주광역시|광주).*", "광주")
                    .replaceAll("^(대전광역시|대전).*", "대전")
                    .replaceAll("^(울산광역시|울산).*", "울산")
                    .replaceAll("^(세종특별자치시|세종).*", "세종")
                    .replaceAll("^(강원특별자치도|강원도|강원).*", "강원")
                    .replaceAll("^(충청북도|충북).*", "충북")
                    .replaceAll("^(충청남도|충남).*", "충남")
                    .replaceAll("^(전라북도|전북).*", "전북")
                    .replaceAll("^(전라남도|전남).*", "전남")
                    .replaceAll("^(경상북도|경북).*", "경북")
                    .replaceAll("^(경상남도|경남).*", "경남")
                    .trim();

            locMap.put(upper, locMap.getOrDefault(upper, 0L) + cnt);
        }

        /* ⭐ 2) 카운트 기준 내림차순 정렬 */
        List<Map<String, Object>> sorted =
                locMap.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .map(e -> {
                            Map<String, Object> m = new HashMap<>();
                            m.put("key", e.getKey());
                            m.put("value", e.getValue());
                            return m;
                        })
                        .toList();

        /* ⭐ 3) TOP5 + OTHERS 분리 */
        List<Map<String, Object>> top = sorted.stream().limit(5).toList();
        List<Map<String, Object>> others = sorted.stream().skip(5).toList();

        return Map.of(
                "top", top,
                "others", others
        );
    }

    /* ----------------------------------------
       4) 인승 Bucket (2 이하 / 3 / 4 ... / 10 이상)
       ---------------------------------------- */
    private long countCap(String cond) {

        String sql = """
        SELECT COUNT(*)
        FROM (
            SELECT cs.capacity
            FROM all_car_sale a
            JOIN car_sale cs
              ON cs.all_car_sale_id = a.car_id OR cs.car_id = a.car_id
            WHERE a.is_eco_friendly = 1 
              AND cs.capacity %s 
              AND cs.capacity IS NOT NULL

            UNION ALL

            SELECT ims.capacity
            FROM all_car_sale a
            JOIN import_car_sale ims
              ON ims.all_car_sale_id = a.car_id OR ims.car_id = a.car_id
            WHERE a.is_eco_friendly = 1 
              AND ims.capacity %s
              AND ims.capacity IS NOT NULL
        ) t
    """.formatted(cond, cond);

        Object x = em.createNativeQuery(sql).getSingleResult();
        return ((Number)x).longValue();
    }

    public Map<String, Long> countCapacityBuckets() {

        return Map.of(
                "le2",  countCap("<= 2"),
                "eq3",  countCap("= 3"),
                "eq4",  countCap("= 4"),
                "eq5",  countCap("= 5"),
                "eq6",  countCap("= 6"),
                "eq7",  countCap("= 7"),
                "eq8",  countCap("= 8"),
                "eq9",  countCap("= 9"),
                "ge10", countCap(">= 10")
        );
    }
}
