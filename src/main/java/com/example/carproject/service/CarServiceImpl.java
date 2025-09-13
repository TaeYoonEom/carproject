// com/example/carproject/service/CarServiceImpl.java
package com.example.carproject.service;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CarServiceImpl implements CarService {

    private final JdbcTemplate jdbc;

    public CarServiceImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /* =======================
        카테고리 판단 (임시 규칙 그대로 유지)
       ======================= */
    private Category categoryOf(Long carId) {
        int id = carId == null ? 0 : carId.intValue();
        if (1 <= id && id <= 15) return Category.KOREAN;
        if (16 <= id && id <= 30) return Category.EV;
        if (31 <= id && id <= 45) return Category.FOREIGN;
        return Category.UNKNOWN;
    }
    enum Category {
        KOREAN("국산차", "/korean"),
        EV("전기차", "/ev"),
        FOREIGN("수입차", "/foreign"),
        UNKNOWN("차량", "/");
        final String name; final String path;
        Category(String n, String p){ this.name=n; this.path=p; }
    }

    /* =======================
        차량 상세 (임시 샘플 – 추후 DB 연동)
       ======================= */
    @Override
    public CarDto getCarDetail(Long carId) {
        final String sql = """
        SELECT
            car_id,
            car_name,
            year,
            mileage,
            price,
            fuel_type,
            transmission,
            drive_type,
            exterior_color,
            interior_color,
            sale_location,
            ownership_status,
            seller_type,
            car_number,
            capacity,
            sale_type,
            created_at
        FROM car_sale
        WHERE car_id = ?
        LIMIT 1
    """;

        return jdbc.query(con -> {
            var ps = con.prepareStatement(sql);
            ps.setLong(1, carId);
            return ps;
        }, rs -> {
            if (!rs.next()) {
                throw new NoSuchElementException("car_sale not found. car_id=" + carId);
            }
            CarDto dto = new CarDto();
            dto.setCarId(rs.getLong("car_id"));
            dto.setCarName(rs.getString("car_name"));
            dto.setYear((Integer) rs.getObject("year"));                 // INT → Integer
            dto.setMileage((Integer) rs.getObject("mileage"));           // INT → Integer
            dto.setPrice((Integer) rs.getObject("price"));               // INT → Integer
            dto.setFuelType(rs.getString("fuel_type"));
            dto.setTransmission(rs.getString("transmission"));
            dto.setDriveType(rs.getString("drive_type"));
            dto.setExteriorColor(rs.getString("exterior_color"));
            dto.setInteriorColor(rs.getString("interior_color"));
            dto.setSaleLocation(rs.getString("sale_location"));
            dto.setOwnershipStatus(rs.getString("ownership_status"));    // ENUM → String
            dto.setSellerType(rs.getString("seller_type"));              // ENUM → String
            dto.setCarNumber(rs.getString("car_number"));
            dto.setCapacity((Integer) rs.getObject("capacity"));         // cc (nullable)
            dto.setSaleType(rs.getString("sale_type"));                  // 예: 일반/리스/렌트…
            var ts = rs.getTimestamp("created_at");
            dto.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
            return dto;
        });
    }

    /* =======================
        이미지: DB에서 조회
       ======================= */

    // 대표 이미지: DB에서만. 없으면 null 반환
    @Override
    public String getMainImage(Long carId) {
        String sqlRepFront = """
            SELECT COALESCE(front_view_url, left_side_url, right_side_url,
                            rear_view_url, driver_seat_url, back_seat_url)
              FROM car_image
             WHERE car_id = ? AND is_representative = 1
             ORDER BY uploaded_at DESC
             LIMIT 1
        """;
        List<String> rep = jdbc.query(sqlRepFront, (rs, n) -> rs.getString(1), carId);
        if (!rep.isEmpty() && rep.get(0) != null && !rep.get(0).isBlank()) return rep.get(0);

        // 대표가 없으면, 전체 중 최신 1장
        String sqlAny = """
            SELECT COALESCE(front_view_url, left_side_url, right_side_url,
                            rear_view_url, driver_seat_url, back_seat_url)
              FROM car_image
             WHERE car_id = ?
             ORDER BY is_representative DESC, uploaded_at DESC
             LIMIT 1
        """;
        List<String> any = jdbc.query(sqlAny, (rs, n) -> rs.getString(1), carId);
        return (!any.isEmpty() && any.get(0) != null && !any.get(0).isBlank()) ? any.get(0) : null;
    }

    // 전체 갤러리: DB에서만. 없으면 빈 리스트 반환
    @Override
    public List<String> getAllImages(Long carId) {
        String sql = """
            SELECT front_view_url, left_side_url, right_side_url,
                   rear_view_url,  driver_seat_url, back_seat_url
              FROM car_image
             WHERE car_id = ?
             ORDER BY is_representative DESC, uploaded_at DESC
        """;

        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        jdbc.query(sql, rs -> {
            String front  = rs.getString("front_view_url");
            String left   = rs.getString("left_side_url");
            String right  = rs.getString("right_side_url");
            String rear   = rs.getString("rear_view_url");
            String driver = rs.getString("driver_seat_url");
            String back   = rs.getString("back_seat_url");

            if (front  != null && !front.isBlank())  ordered.add(front);
            if (left   != null && !left.isBlank())   ordered.add(left);
            if (right  != null && !right.isBlank())  ordered.add(right);
            if (rear   != null && !rear.isBlank())   ordered.add(rear);
            if (driver != null && !driver.isBlank()) ordered.add(driver);
            if (back   != null && !back.isBlank())   ordered.add(back);
        }, carId);

        // 메인을 맨 앞으로 정렬
        String main = getMainImage(carId);
        if (ordered.isEmpty()) return List.of();   // 빈 리스트 반환

        List<String> out = new ArrayList<>();
        if (main != null) {
            out.add(main);
            ordered.remove(main);
        }
        out.addAll(ordered);
        return out;
    }

    /* =======================
        기타(그대로 유지: 샘플 데이터)
       ======================= */
    @Override public List<String> getOptions(Long carId) { return List.of("내비게이션", "스마트키", "후방카메라"); }

    @Override public Map<String, String> getInspectionMap(Long carId) {
        return Map.of("엔진오일","정상","브레이크","정상","타이어","70% 이상");
    }

    @Override public String getInspectionImage(Long carId) { return null; }

    @Override public InsuranceDto getInsurance(Long carId) {
        InsuranceDto dto = new InsuranceDto();
        dto.setAccidents(0); dto.setTotalLoss(0); dto.setFlood(0); dto.setPanels(0); dto.setCost(0);
        return dto;
    }

    @Override public List<ComparableCarDto> getComparables(CarDto car) { return Collections.emptyList(); }

    @Override
    public SellerDto getSeller(Long carId) {
        final String sql = """
        SELECT m.name, m.phone, m.email, m.address, m.is_address_public, m.joined_at
          FROM member m
          JOIN car_sale s ON s.member_id = m.member_id
         WHERE s.car_id = ?
         LIMIT 1
    """;

        List<SellerDto> list = jdbc.query(sql, ps -> ps.setLong(1, carId), (rs, n) -> {
            SellerDto dto = new SellerDto();
            dto.setName(rs.getString("name"));
            dto.setPhone(rs.getString("phone"));
            dto.setEmail(rs.getString("email"));
            if (rs.getObject("is_address_public") != null && rs.getInt("is_address_public") == 1) {
                dto.setAddress(rs.getString("address"));
            }
            var ts = rs.getTimestamp("joined_at");
            dto.setJoinedAt(ts != null ? ts.toLocalDateTime() : null);
            dto.setStore(null);
            return dto;
        });

        return list.isEmpty() ? null : list.get(0);
    }



    @Override public String getCategoryName(Long carId) { return categoryOf(carId).name; }
    @Override public String getCategoryPath(Long carId) { return categoryOf(carId).path; }
}
