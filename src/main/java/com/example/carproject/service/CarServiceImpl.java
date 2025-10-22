// com/example/carproject/service/CarServiceImpl.java
package com.example.carproject.service;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.carproject.repository.ImportCarSaleRepository2;


import java.util.*;

@Service
public class CarServiceImpl implements CarService {

    private final JdbcTemplate jdbc;
    private final ImportCarSaleRepository2 importCarSaleRepository;

    public CarServiceImpl(JdbcTemplate jdbc, ImportCarSaleRepository2 importCarSaleRepository) {
        this.jdbc = jdbc;
        this.importCarSaleRepository = importCarSaleRepository;
    }

    private enum Category {
        KOREAN("국산차", "/korean"),
        EV("전기차", "/ev"),
        FOREIGN("수입차", "/foreign"),
        UNKNOWN("차량", "/");
        final String name; final String path;
        Category(String n, String p){ this.name=n; this.path=p; }
    }

    /** ✅ all_car_sale 테이블을 기준으로 카테고리 판별 */
    private Category categoryOf(Long carId) {
        final String sql = """
        SELECT 
          CAST(origin AS SIGNED)            AS origin,
          CAST(is_eco_friendly AS SIGNED)   AS eco
        FROM all_car_sale
        WHERE car_id = ?
        LIMIT 1
    """;

        return jdbc.query(con -> {
            var ps = con.prepareStatement(sql);
            ps.setLong(1, carId);
            return ps;
        }, rs -> {
            if (!rs.next()) return Category.UNKNOWN;

            Integer origin = rs.getObject("origin", Integer.class);
            Integer eco    = rs.getObject("eco", Integer.class);

            if (eco != null && eco == 1) return Category.EV; // ⚡ EV 우선
            if (origin != null) {
                if (1 <= origin && origin <= 30)  return Category.KOREAN;
                if (31 <= origin && origin <= 45) return Category.FOREIGN;
            }
            return Category.UNKNOWN;
        });
    }

    @Override public String getCategoryName(Long carId) { return categoryOf(carId).name; }
    @Override public String getCategoryPath(Long carId) { return categoryOf(carId).path; }


    // 타입 안전 정수 변환
    private static Integer toInt(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        if (o instanceof String s && !s.isBlank()) return Integer.valueOf(s.trim());
        return null;
    }

    // 공통 매퍼
    private CarDto mapCarRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        CarDto dto = new CarDto();
        dto.setCarId(rs.getLong("car_id"));
        dto.setCarName(rs.getString("car_name"));
        dto.setYear(toInt(rs.getObject("year")));
        dto.setMileage(toInt(rs.getObject("mileage")));
        dto.setPrice(toInt(rs.getObject("price")));
        dto.setFuelType(rs.getString("fuel_type"));
        dto.setTransmission(rs.getString("transmission"));
        dto.setDriveType(rs.getString("drive_type"));
        dto.setExteriorColor(rs.getString("exterior_color"));
        dto.setInteriorColor(rs.getString("interior_color"));
        dto.setSaleLocation(rs.getString("sale_location"));
        dto.setOwnershipStatus(rs.getString("ownership_status"));
        dto.setSellerType(rs.getString("seller_type"));
        dto.setCarNumber(rs.getString("car_number"));
        dto.setCapacity(toInt(rs.getObject("capacity")));
        dto.setSaleType(rs.getString("sale_type"));
        var ts = rs.getTimestamp("created_at");
        dto.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return dto;
    }

    @Override
    public CarDto getCarDetail(Long carId) {
        // 1️⃣ origin 확인
        final String ORIGIN_SQL = """
        SELECT origin
        FROM all_car_sale
        WHERE car_id = ?
        LIMIT 1
    """;
        Integer origin = jdbc.query(con -> {
            var ps = con.prepareStatement(ORIGIN_SQL);
            ps.setLong(1, carId);
            return ps;
        }, rs -> rs.next() ? rs.getInt("origin") : null);

        if (origin == null)
            throw new NoSuchElementException("🚫 all_car_sale에서 origin을 찾을 수 없습니다. car_id=" + carId);

        // 2️⃣ origin별 대상 테이블 분기
        String targetTable = (origin == 0) ? "car_sale" : "import_car_sale";

        // 3️⃣ ✅ all_car_sale_id 기준으로 조회
        final String SELECT_BY_ALL_ID = """
        SELECT 
            car_id, car_name, year, mileage, price, fuel_type, transmission, drive_type,
            exterior_color, interior_color, sale_location, ownership_status, seller_type,
            car_number, capacity, sale_type, created_at
        FROM %s
        WHERE all_car_sale_id = ?
        LIMIT 1
    """.formatted(targetTable);

        List<CarDto> result = jdbc.query(SELECT_BY_ALL_ID,
                ps -> ps.setLong(1, carId),
                (rs, n) -> mapCarRow(rs));

        if (result.isEmpty())
            throw new NoSuchElementException("🚫 " + targetTable + "에서 차량 정보를 찾을 수 없습니다. all_car_sale_id=" + carId);

        CarDto dto = result.get(0);
        dto.setCategoryName((origin == 0) ? "국산차" : "수입차");
        dto.setCategoryPath((origin == 0) ? "/korean" : "/foreign");
        return dto;
    }



    @Override
    public CarDto getImportCarDetail(Long carId) {
        return importCarSaleRepository.findImportCarByCarId(carId)
                .map(sale -> {
                    CarDto dto = new CarDto();
                    dto.setCarId(sale.getCarId().longValue());
                    dto.setCarName(sale.getCarName());
                    dto.setYear(sale.getYear());
                    dto.setMileage(sale.getMileage());
                    dto.setPrice(sale.getPrice());
                    dto.setFuelType(sale.getFuelType());
                    dto.setTransmission(sale.getTransmission());
                    dto.setDriveType(sale.getDriveType());
                    dto.setExteriorColor(sale.getExteriorColor());
                    dto.setInteriorColor(sale.getInteriorColor());
                    dto.setSaleLocation(sale.getSaleLocation());
                    dto.setOwnershipStatus(sale.getOwnershipStatus());
                    dto.setSellerType(sale.getSellerType());
                    dto.setCarNumber(sale.getCarNumber());
                    dto.setSaleType(sale.getSaleType());
                    dto.setCapacity(sale.getCapacity());
                    dto.setCreatedAt(sale.getCreatedAt());

                    // ✅ 카테고리 정보 (수입차 기준)
                    dto.setCategoryName("수입차");
                    dto.setCategoryPath("/foreign");

                    return dto;
                })
                .orElseThrow(() -> new IllegalArgumentException("수입 차량 정보를 찾을 수 없습니다."));
    }




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
    @Override
    public List<String> getOptions(Long carId) {
        List<String> options = new ArrayList<>();

        // 1) 외관/내장 옵션
        String sqlExterior = "SELECT * FROM car_option_exterior WHERE car_id = ? ORDER BY uploaded_at DESC LIMIT 1";
        jdbc.query(sqlExterior, rs -> {
            if (rs.getInt("sunroof") == 1) options.add("선루프");
            if (rs.getInt("hid_led_headlamp") == 1) options.add("헤드램프(HID, LED)");
            if (rs.getInt("power_trunk") == 1) options.add("파워 전동 트렁크");
            if (rs.getInt("ghost_door_closing") == 1) options.add("고스트 도어 클로징");
            if (rs.getInt("auto_side_mirror") == 1) options.add("전동접이 사이드 미러");
            if (rs.getInt("aluminum_wheel") == 1) options.add("알루미늄 휠");
            if (rs.getInt("roof_rack") == 1) options.add("루프랙");
            if (rs.getInt("heated_steering_wheel") == 1) options.add("열선 스티어링 휠");
            if (rs.getInt("adjustable_steering_wheel") == 1) options.add("전동 조절 스티어링 휠");
            if (rs.getInt("paddle_shift") == 1) options.add("패들 시프트");
            if (rs.getInt("steering_remote") == 1) options.add("스티어링 휠 리모컨");
            if (rs.getInt("ecm_mirror") == 1) options.add("ECM 룸미러");
            if (rs.getInt("hi_pass") == 1) options.add("하이패스");
            if (rs.getInt("power_doorlock") == 1) options.add("파워 도어록");
            if (rs.getInt("power_steering") == 1) options.add("파워 스티어링 휠");
            if (rs.getInt("power_window") == 1) options.add("파워 윈도우");
        }, carId);

        // 2) 편의 옵션
        String sqlConv = "SELECT * FROM car_option_convenience WHERE car_id = ? ORDER BY uploaded_at DESC LIMIT 1";
        jdbc.query(sqlConv, rs -> {
            if (rs.getInt("massage_seat") == 1) options.add("마사지 시트");
            if (rs.getInt("ventilated_seat") == 1) options.add("통풍 시트");
            if (rs.getInt("memory_seat") == 1) options.add("메모리 시트");
            if (rs.getInt("heated_seat") == 1) options.add("열선 시트");
            if (rs.getInt("power_seat") == 1) options.add("전동 시트");
            if (rs.getInt("fabric_seat") == 1) options.add("직물 시트");
            if (rs.getInt("aux_port") == 1) options.add("AUX 단자");
            if (rs.getInt("usb_port") == 1) options.add("USB 단자");
            if (rs.getInt("cd_player") == 1) options.add("CD 플레이어");
            if (rs.getInt("bluetooth") == 1) options.add("블루투스");
            if (rs.getInt("av_monitor_rear") == 1) options.add("후석 AV 모니터");
            if (rs.getInt("av_monitor_front") == 1) options.add("전석 AV 모니터");
            if (rs.getInt("navigation") == 1) options.add("내비게이션");
            if (rs.getInt("curtain") == 1) options.add("커튼");
            if (rs.getInt("auto_light") == 1) options.add("자동 라이트");
            if (rs.getInt("hud") == 1) options.add("HUD");
            if (rs.getInt("epb") == 1) options.add("전자식 파킹 브레이크(EPB)");
            if (rs.getInt("cruise_control") == 1) options.add("크루즈 컨트롤");
            if (rs.getInt("auto_aircon") == 1) options.add("자동 에어컨");
            if (rs.getInt("smart_key") == 1) options.add("스마트 키");
            if (rs.getInt("remote_key") == 1) options.add("리모트 키");
            if (rs.getInt("rain_sensor") == 1) options.add("레인 센서");
        }, carId);

        // 3) 안전 옵션
        String sqlSafety = "SELECT * FROM car_option_safety WHERE car_id = ? ORDER BY uploaded_at DESC LIMIT 1";
        jdbc.query(sqlSafety, rs -> {
            if (rs.getInt("airbag_front") == 1) options.add("앞 에어백");
            if (rs.getInt("airbag_side") == 1) options.add("사이드 에어백");
            if (rs.getInt("airbag_curtain") == 1) options.add("커튼 에어백");
            if (rs.getInt("abs") == 1) options.add("ABS");
            if (rs.getInt("tcs") == 1) options.add("TCS");
            if (rs.getInt("esc") == 1) options.add("ESC");
            if (rs.getInt("tpms") == 1) options.add("TPMS");
            if (rs.getInt("ldws") == 1) options.add("LDWS(차선 이탈 경고)");
            if (rs.getInt("ecs") == 1) options.add("ECS(전자제어 서스펜션)");
            if (rs.getInt("parking_sensor") == 1) options.add("주차 감지 센서");
            if (rs.getInt("rear_warning") == 1) options.add("후방 경고");
            if (rs.getInt("rear_camera") == 1) options.add("후방 카메라");
            if (rs.getInt("around_view") == 1) options.add("어라운드 뷰");
        }, carId);

        // 4) 시트 옵션
        String sqlSeat = "SELECT * FROM car_option_seat WHERE car_id = ? ORDER BY uploaded_at DESC LIMIT 1";
        jdbc.query(sqlSeat, rs -> {
            if (rs.getInt("family_seat") == 1) options.add("패밀리 시트");
            if (rs.getInt("power_seat_front") == 1) options.add("앞좌석 전동 시트");
            if (rs.getInt("power_seat_rear") == 1) options.add("뒷좌석 전동 시트");
            if (rs.getInt("heated_seat_front") == 1) options.add("앞좌석 열선 시트");
            if (rs.getInt("heated_seat_rear") == 1) options.add("뒷좌석 열선 시트");
            if (rs.getInt("memory_seat") == 1) options.add("메모리 시트");
            if (rs.getInt("ventilated_seat_front") == 1) options.add("앞좌석 통풍 시트");
            if (rs.getInt("ventilated_seat_rear") == 1) options.add("뒷좌석 통풍 시트");
            if (rs.getInt("massage_seat") == 1) options.add("마사지 시트");
        }, carId);

        return options;
    }


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
          JOIN (
                SELECT member_id, car_id FROM car_sale
                UNION ALL
                SELECT member_id, car_id FROM import_car_sale
          ) s ON s.member_id = m.member_id
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

}
