package com.example.carproject.buy.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ElectricFacetRepository {

    private final EntityManager em;

    // ==============================
    // 공통 Facet Helper
    // ==============================
    private List<Map<String, Object>> runFacetQuery(String column) {

        String sql = String.format("""
            SELECT %s AS val, COUNT(*) AS cnt
            FROM eco_car_flat
            WHERE %s IS NOT NULL
            GROUP BY %s
            ORDER BY cnt DESC
        """, column, column, column);

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

    // ==============================
    // ✅ Facet 목록
    // ==============================
    public List<Map<String, Object>> countCarType()       { return runFacetQuery("car_type"); }
    public List<Map<String, Object>> countFuelType()      { return runFacetQuery("fuel_type"); }
    public List<Map<String, Object>> countManufacturer()  { return runFacetQuery("manufacturer"); }
    public List<Map<String, Object>> countModelName()     { return runFacetQuery("model_name"); }
    public List<Map<String, Object>> countCarName()       { return runFacetQuery("car_name"); }
    public List<Map<String, Object>> countExteriorColor() { return runFacetQuery("exterior_color"); }
    public List<Map<String, Object>> countInteriorColor() { return runFacetQuery("interior_color"); }
    public List<Map<String, Object>> countTransmission()  { return runFacetQuery("transmission"); }
    public List<Map<String, Object>> countSellerType()    { return runFacetQuery("seller_type"); }
    public List<Map<String, Object>> countSaleMethod()    { return runFacetQuery("sale_method"); }
    public List<Map<String, Object>> countPerformance()   { return runFacetQuery("performance_open"); }


    // ==============================
    // ✅ 지역 TOP + Others
    // ==============================
    public Map<String, Object> countRegion() {

        String sql = """
            SELECT sale_location, COUNT(*) AS cnt
            FROM eco_car_flat
            WHERE sale_location IS NOT NULL
            GROUP BY sale_location
            ORDER BY cnt DESC
        """;

        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        Map<String, Long> map = new HashMap<>();

        for (Object[] r : rows) {
            String raw = r[0].toString();
            long cnt = ((Number) r[1]).longValue();

            // ✅ 지역 통합
            String key = raw
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

            map.put(key, map.getOrDefault(key, 0L) + cnt);
        }

        List<Map<String, Object>> sorted =
                map.entrySet().stream()
                        .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                        .map(e -> {
                            Map<String, Object> m = new HashMap<>();
                            m.put("key", e.getKey());
                            m.put("value", e.getValue());
                            return m;
                        })
                        .toList();

        return Map.of(
                "top",    sorted.stream().limit(5).toList(),
                "others", sorted.stream().skip(5).toList()
        );
    }

    // ==============================
    // ✅ 인승 Bucket
    // ==============================
    private long cap(String cond) {
        String sql = """
            SELECT COUNT(*)
            FROM eco_car_flat
            WHERE capacity %s AND capacity IS NOT NULL
        """.formatted(cond);

        return ((Number) em.createNativeQuery(sql).getSingleResult()).longValue();
    }

    public Map<String, Long> countCapacity() {
        return Map.of(
                "le2",  cap("<= 2"),
                "eq3",  cap("= 3"),
                "eq4",  cap("= 4"),
                "eq5",  cap("= 5"),
                "eq6",  cap("= 6"),
                "eq7",  cap("= 7"),
                "eq8",  cap("= 8"),
                "eq9",  cap("= 9"),
                "ge10", cap(">= 10")
        );
    }
}
