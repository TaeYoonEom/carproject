package com.example.carproject.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class MarketPriceRepository {

    private final NamedParameterJdbcTemplate jdbc;

    private final String baseSelect = """
      SELECT id, manufacturer, model_name AS modelName, year, mileage_km AS mileageKm,
             region, fuel_type AS fuelType, exterior_color AS exteriorColor,
             thumbnail_url AS thumbnailUrl, price_manwon AS priceManwon, created_at
      FROM v_market_car
      WHERE 1=1
    """;

    private MapSqlParameterSource bind(Map<String,?> m){ return new MapSqlParameterSource(m); }

    // where 절 공통 바인딩
    private void addWhere(StringBuilder sql, Map<String,Object> p, Map<String,Object> f){
        if (has(f,"make"))       { sql.append(" AND manufacturer = :make");          p.put("make", f.get("make")); }
        if (has(f,"model"))      { sql.append(" AND model_name = :model");           p.put("model", f.get("model")); }
        if (has(f,"yearFrom"))   { sql.append(" AND year >= :yf");                   p.put("yf", f.get("yearFrom")); }
        if (has(f,"yearTo"))     { sql.append(" AND year <= :yt");                   p.put("yt", f.get("yearTo")); }
        if (has(f,"priceMin"))   { sql.append(" AND price_manwon >= :pmin");         p.put("pmin", f.get("priceMin")); }
        if (has(f,"priceMax"))   { sql.append(" AND price_manwon <= :pmax");         p.put("pmax", f.get("priceMax")); }
        if (has(f,"mileageMin")) { sql.append(" AND mileage_km >= :mmin");           p.put("mmin", f.get("mileageMin")); }
        if (has(f,"mileageMax")) { sql.append(" AND mileage_km <= :mmax");           p.put("mmax", f.get("mileageMax")); }
        if (has(f,"fuel"))       { sql.append(" AND fuel_type IN (:fuel)");          p.put("fuel", f.get("fuel")); }
        if (has(f,"colors"))     { sql.append(" AND exterior_color IN (:colors)");   p.put("colors", f.get("colors")); }
    }

    // 리스트 조회 + 페이징 + 정렬
    public List<Map<String,Object>> search(Map<String,Object> f, int page, int size, String sort){
        StringBuilder sql = new StringBuilder(baseSelect);
        Map<String,Object> p = new HashMap<>();
        addWhere(sql, p, f);

        switch (sort == null ? "RECENT" : sort){
            case "PRICE_ASC"   -> sql.append(" ORDER BY price_manwon ASC, id DESC");
            case "PRICE_DESC"  -> sql.append(" ORDER BY price_manwon DESC, id DESC");
            case "MILEAGE_ASC" -> sql.append(" ORDER BY mileage_km ASC, id DESC");
            default            -> sql.append(" ORDER BY created_at DESC, id DESC");
        }
        sql.append(" LIMIT :limit OFFSET :offset");
        p.put("limit", size);
        p.put("offset", page * size);

        return jdbc.queryForList(sql.toString(), bind(p));
    }

    // 총 개수
    public int count(Map<String,Object> f){
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM v_market_car WHERE 1=1");
        Map<String,Object> p = new HashMap<>();
        addWhere(sql, p, f);
        return jdbc.queryForObject(sql.toString(), bind(p), Integer.class);
    }

    // 통계
    public Map<String,Object> stats(Map<String,Object> f){
        StringBuilder sql = new StringBuilder("""
          SELECT MIN(price_manwon) AS minManwon,
                 MAX(price_manwon) AS maxManwon,
                 CAST(AVG(price_manwon) AS SIGNED) AS avgManwon,
                 COUNT(*) AS totalCount
          FROM v_market_car WHERE 1=1
        """);
        Map<String,Object> p = new HashMap<>();
        addWhere(sql, p, f);
        return jdbc.queryForMap(sql.toString(), bind(p));
    }

    // 제조사 목록(+개수)
    public List<Map<String,Object>> makeCounts(){
        return jdbc.queryForList("""
          SELECT manufacturer AS name, COUNT(*) AS count
          FROM v_market_car
          GROUP BY manufacturer
          ORDER BY manufacturer
        """, Map.of());
    }

    // 모델 목록(+개수)
    public List<Map<String,Object>> modelCounts(String make){
        if (make == null || make.isBlank()){
            return jdbc.queryForList("""
              SELECT model_name AS name, COUNT(*) AS count
              FROM v_market_car
              GROUP BY model_name
              ORDER BY model_name
            """, Map.of());
        }
        return jdbc.queryForList("""
          SELECT model_name AS name, COUNT(*) AS count
          FROM v_market_car
          WHERE manufacturer = :m
          GROUP BY model_name
          ORDER BY model_name
        """, Map.of("m", make));
    }

    // 필요하면 문자열-only 버전도 유지
    public List<String> makes(){
        return jdbc.queryForList("SELECT DISTINCT manufacturer FROM v_market_car ORDER BY manufacturer", Map.of(), String.class);
    }
    public List<String> models(String make){
        if (make == null || make.isBlank()){
            return jdbc.queryForList("SELECT DISTINCT model_name FROM v_market_car ORDER BY model_name", Map.of(), String.class);
        }
        return jdbc.queryForList("SELECT DISTINCT model_name FROM v_market_car WHERE manufacturer=:m ORDER BY model_name",
                Map.of("m", make), String.class);
    }

    private boolean has(Map<String,?> f, String k){
        Object v = f.get(k);
        if (v == null) return false;
        if (v instanceof String s) return !s.isBlank();
        if (v instanceof List<?> l) return !l.isEmpty();
        return true;
    }
}
