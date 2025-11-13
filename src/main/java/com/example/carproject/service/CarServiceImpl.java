// com/example/carproject/service/CarServiceImpl.java
package com.example.carproject.service;

import com.example.carproject.dto.CarDto;
import com.example.carproject.dto.ComparableCarDto;
import com.example.carproject.dto.InsuranceDto;
import com.example.carproject.dto.SellerDto;
import com.example.carproject.repository.AllCarSaleRepository2;
import com.example.carproject.repository.CarEntryDraftRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.carproject.repository.ImportCarSaleRepository2;

import java.util.*;

@Service
public class CarServiceImpl implements CarService {

    private final JdbcTemplate jdbc;
    private final ImportCarSaleRepository2 importCarSaleRepository;
    private final AllCarSaleRepository2 allCarSaleRepository;
    private final CarEntryDraftRepository carEntryDraftRepository;

    public CarServiceImpl(
            JdbcTemplate jdbc,
            ImportCarSaleRepository2 importCarSaleRepository,
            AllCarSaleRepository2 allCarSaleRepository,
            CarEntryDraftRepository carEntryDraftRepository
    ) {
        this.jdbc = jdbc;
        this.importCarSaleRepository = importCarSaleRepository;
        this.allCarSaleRepository = allCarSaleRepository;
        this.carEntryDraftRepository = carEntryDraftRepository;
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
       1) 옵션: Draft 기반
       ======================= */
    @Override
    public List<String> getOptions(Long carId) {

        // 🔗 all_car_sale → draft id
        var all = allCarSaleRepository.findByCarId(carId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("all_car_sale 없음: car_id=" + carId));

        Integer draftId = all.getCarEntryDraftId();
        if (draftId == null) {
            return List.of(); // draft가 없으면 옵션 없음
        }

        var d = carEntryDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("draft 없음: id=" + draftId));

        List<String> opts = new ArrayList<>();

        if (d.getCarGrade() != null && !d.getCarGrade().isBlank())
            opts.add("등급: " + d.getCarGrade());
        if (d.getFuelType() != null && !d.getFuelType().isBlank())
            opts.add("연료: " + d.getFuelType());
        if (d.getTransmission() != null && !d.getTransmission().isBlank())
            opts.add("변속기: " + d.getTransmission());
        if (d.getDriveType() != null && !d.getDriveType().isBlank())
            opts.add("구동방식: " + d.getDriveType());
        if (d.getExteriorColor() != null && !d.getExteriorColor().isBlank())
            opts.add("외부 색상: " + d.getExteriorColor());
        if (d.getInteriorColor() != null && !d.getInteriorColor().isBlank())
            opts.add("내부 색상: " + d.getInteriorColor());
        if (d.getSeatColor() != null && !d.getSeatColor().isBlank())
            opts.add("시트 색상: " + d.getSeatColor());

        return opts;
    }

    /* =======================
       2) 성능기록부: Draft 기반
       ======================= */
    @Override
    public Map<String, String> getInspectionMap(Long carId) {

        var all = allCarSaleRepository.findByCarId(carId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("all_car_sale 없음: car_id=" + carId));

        Integer draftId = all.getCarEntryDraftId();
        if (draftId == null) {
            return Collections.emptyMap();
        }

        var d = carEntryDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("draft 없음: id=" + draftId));

        Map<String, String> m = new LinkedHashMap<>();

        // ⚠️ boolean / Integer 필드는 엔티티 타입에 따라 primitive일 수도 있어서 그대로 사용
        m.put("타이어 잔량",
                d.getTirePercentage() == null ? "-" : d.getTirePercentage() + "%");
        m.put("엔진오일 이상", d.getEngineOilIssue() ? "예" : "아니오");
        m.put("브레이크 이상", d.getBrakeIssue() ? "예" : "아니오");
        m.put("성능점검 실시", d.getPerformanceChecked() ? "예" : "아니오");
        m.put("사고 수리", d.getAccidentRepairCnt() + "회");
        m.put("전손", d.getTotalLossCnt() + "회");
        m.put("침수", d.getFloodCnt() + "회");
        m.put("판금 교환", d.getPanelReplacementCnt() + "회");
        m.put("보험처리 비용", d.getInsuranceClaimCost() + "원");
        m.put("타차가해", d.getThirdPartyDamage() ? "예" : "아니오");
        m.put("판금", d.getPanelBeating() ? "예" : "아니오");
        m.put("국소 교환", d.getReplacementMinor() ? "예" : "아니오");
        m.put("부식", d.getCorrosion() ? "예" : "아니오");
        m.put("특이사항",
                (d.getSpecialNote() == null || d.getSpecialNote().isBlank())
                        ? "-" : d.getSpecialNote());

        return m;
    }

    @Override
    public String getInspectionImage(Long carId) {
        // 지금은 성능기록부 이미지를 따로 안 쓰고 있으니 null
        return null;
    }

    /* =======================
       3) 보험이력: Draft 기반
       ======================= */
    @Override
    public InsuranceDto getInsurance(Long carId) {

        var all = allCarSaleRepository.findByCarId(carId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("all_car_sale 없음: car_id=" + carId));

        Integer draftId = all.getCarEntryDraftId();
        if (draftId == null) {
            return null; // 보험이력 없음
        }

        var d = carEntryDraftRepository.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("draft 없음: id=" + draftId));

        InsuranceDto dto = new InsuranceDto();
        dto.setAccidents(d.getAccidentRepairCnt());
        dto.setTotalLoss(d.getTotalLossCnt());
        dto.setFlood(d.getFloodCnt());
        dto.setPanels(d.getPanelReplacementCnt());
        dto.setCost(d.getInsuranceClaimCost());

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
