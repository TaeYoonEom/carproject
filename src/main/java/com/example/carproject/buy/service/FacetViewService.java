package com.example.carproject.buy.service;

import com.example.carproject.buy.repository.CarSaleRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// FacetViewService.java
@Service
@RequiredArgsConstructor
public class FacetViewService {
    private final CarSaleRepository repo;

    public LinkedHashMap<String, Long> carTypeCountsZero() {
        // 1️⃣ 고정된 표기 순서 (UI 표시 순서)
        List<String> order = List.of(
                "경차","소형차","준중형차","중형차","대형차","스포츠카",
                "SUV","RV","경승합차","승합차","화물차","기타"
        );
        Set<String> KNOWN = new HashSet<>(order);

        // 2️⃣ 버킷 초기화
        Map<String, Long> bucket = new HashMap<>();

        // 3️⃣ DB 결과 순회 → 정규화 후 집계
        repo.countByCarType().forEach(r -> {
            String raw = safe(r.getVal());
            String norm = normalizeCarType(raw);
            String key = (norm.isBlank() || !KNOWN.contains(norm)) ? "기타" : norm;
            bucket.merge(key, r.getCnt(), Long::sum);
        });

        // 4️⃣ Zero-fill (없는 항목은 0)
        LinkedHashMap<String, Long> out = new LinkedHashMap<>();
        for (String k : order) out.put(k, bucket.getOrDefault(k, 0L));
        return out;
    }

    //차종 정규화 (공백, 오타, 영문 등 처리)
    private String normalizeCarType(String v) {
        if (v == null) return "";
        String s = v.trim();

        // 대표 치환 규칙
        if (s.equalsIgnoreCase("suv")) return "SUV";
        if (s.equalsIgnoreCase("rv")) return "RV";
        if (s.equalsIgnoreCase("sports") || s.equals("스포츠")) return "스포츠카";
        if (s.equalsIgnoreCase("승합")) return "승합차";
        if (s.equalsIgnoreCase("경승합")) return "경승합차";
        if (s.equalsIgnoreCase("truck") || s.equals("트럭")) return "화물차";

        return s;
    }


    public LinkedHashMap<String, Long> manufacturerCountsWithOthers(int topN) {
        var rows = repo.countByManufacturer(); // List<FacetAgg> (val, cnt)
        // 1) 내림차순 보장되어 있지 않다면 정렬
        rows.sort((a,b) -> Long.compare(b.getCnt(), a.getCnt()));

        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        long others = 0;
        int i = 0;
        for (var r : rows) {
            if (r.getVal() == null || r.getVal().isBlank()) { others += r.getCnt(); continue; }
            if (i < topN) {
                map.put(r.getVal(), r.getCnt());
            } else {
                others += r.getCnt();
            }
            i++;
        }
        // 나머지 묶기
        map.put("기타 제조사", others);
        return map;
    }

    @Getter @AllArgsConstructor
    public static class FacetItem { private String label; private long count; }

    @Getter @AllArgsConstructor
    public static class MakerFacet {      // 제조사 선택 시 내려줄 데이터
        private String maker;
        private List<FacetItem> popularModels; // == modelName Top N
        private List<FacetItem> alphaCarNames; // == carName 이름순(나머지 포함)
    }

    public MakerFacet buildMakerFacet(String maker, int topN) {
        // 1) 인기모델 (modelName) Top N
        var byModelCnt = repo.countModelsByMaker(maker);
        byModelCnt.sort((a,b) -> Long.compare(b.getCnt(), a.getCnt()));
        List<FacetItem> popular = new ArrayList<>();
        int i=0;
        for (var r : byModelCnt) {
            if (r.getVal()==null) continue;
            if (i++ < topN) popular.add(new FacetItem(r.getVal(), r.getCnt()));
        }

        // 2) 이름순 (carName) 전체: 이름순 정렬 + 카운트 매핑
        var carNameCntList = repo.countCarNamesByMaker(maker);
        Map<String, Long> carNameCntMap = new HashMap<>();
        for (var r : carNameCntList) carNameCntMap.put(r.getVal(), r.getCnt());

        List<String> alphaNames = repo.distinctCarNamesByMaker(maker); // 정렬된 이름 목록
        List<FacetItem> alpha = alphaNames.stream()
                .map(n -> new FacetItem(n, carNameCntMap.getOrDefault(n, 0L)))
                .toList();

        return new MakerFacet(maker, popular, alpha);
    }

    /** 지역: 상위 N만 보여주고 나머지는 "더보기"로 처리해도 되고, 일단 전체 */
    public LinkedHashMap<String, Long> saleLocationCounts() {
        var rows = repo.countBySaleLocation();
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();
        for (var r : rows) map.put(r.getVal(), r.getCnt());
        return map; // 0 채우기 필요 없음(고정 리스트가 아니라서)
    }

    /** 인승 버킷 0 포함 */
    public LinkedHashMap<String, Long> capacityBucketCounts() {
        var b = repo.capacityBuckets();
        LinkedHashMap<String, Long> m = new LinkedHashMap<>();
        m.put("2인승 이하", b.getLe2());
        m.put("3인승",     b.getEq3());
        m.put("4인승",     b.getEq4());
        m.put("5인승",     b.getEq5());
        m.put("6인승",     b.getEq6());
        m.put("7인승",     b.getEq7());
        m.put("8인승",     b.getEq8());
        m.put("9인승",     b.getEq9());
        m.put("10인승 이상", b.getGe10());
        return m;
    }

    /** 성능/보험 공개: 0 포함(고정 라벨) */
    public LinkedHashMap<String, Long> performanceOpenCountsZero() {
        List<String> order = List.of("엔카 직영 성능점검","성능기록부","보험이력","차량 이력 공개");
        var raw = repo.countByPerformanceOpen();
        Map<String, Long> tmp = new HashMap<>();
        for (var r : raw) tmp.put(r.getVal(), r.getCnt());

        LinkedHashMap<String, Long> res = new LinkedHashMap<>();
        for (String k : order) res.put(k, tmp.getOrDefault(k, 0L));
        return res;
    }

    /** 판매자 구분: 0 포함(고정 라벨) */
    public LinkedHashMap<String, Long> sellerTypeCountsZero() {
        List<String> order = List.of("개인","딜러","리스렌트제휴");
        return zeroFill(order, repo.countBySellerType());
    }

    /** 판매 방식: 0 포함(고정 라벨) */
    public LinkedHashMap<String, Long> saleMethodCountsZero() {
        List<String> order = List.of("일반","렌트","리스");
        return zeroFill(order, repo.countBySaleMethod());
    }

    /** 외부 색상: 흔한 색상 우선표(없으면 0) */
    public LinkedHashMap<String, Long> exteriorColorCountsZero() {
        List<String> order = List.of("흰색","검정색","쥐색","은색","청색","빨간색","갈색","베이지","녹색","노란색","기타");
        var raw = repo.countByExteriorColor();
        Map<String, Long> tmp = new HashMap<>();
        for (var r : raw) tmp.put(r.getVal(), r.getCnt());

        LinkedHashMap<String, Long> res = new LinkedHashMap<>();
        for (String k : order) res.put(k, tmp.getOrDefault(k, 0L));
        return res;
    }

    // ====== 내부색상: 값 정규화 후 버킷 집계 (0 포함) ======
    public LinkedHashMap<String, Long> interiorColorCountsZero() {
        // 화면 노출 순서
        List<String> order = List.of(
                "검정색 계열","갈색 계열","회색 계열","베이지색 계열","청색 계열","흰색 계열","기타"
        );

        Map<String, Long> bucket = new HashMap<>();
        repo.countByInteriorColor().forEach(r -> {
            String raw = safe(r.getVal());
            String b = mapInteriorColorToBucket(raw);
            bucket.merge(b, r.getCnt(), Long::sum);
        });

        LinkedHashMap<String, Long> out = new LinkedHashMap<>();
        for (String k : order) out.put(k, bucket.getOrDefault(k, 0L));
        return out;
    }

    private String mapInteriorColorToBucket(String v) {
        if (v == null || v.isBlank()) return "기타";
        String s = v.toLowerCase();

        // 검정
        if (s.contains("black") || s.contains("블랙") || s.contains("검정") || s.contains("검은")) return "검정색 계열";
        // 갈색/브라운
        if (s.contains("brown") || s.contains("브라운") || s.contains("갈색") || s.contains("카라멜") || s.contains("탄")) return "갈색 계열";
        // 회색/그레이
        if (s.contains("gray") || s.contains("grey") || s.contains("그레이") || s.contains("회색") || s.contains("쥐색")) return "회색 계열";
        // 베이지/아이보리
        if (s.contains("베이지") || s.contains("ivory") || s.contains("아이보리") || s.contains("크림")) return "베이지색 계열";
        // 청색/블루/네이비
        if (s.contains("blue") || s.contains("블루") || s.contains("청") || s.contains("네이비")) return "청색 계열";
        // 흰색/화이트
        if (s.contains("white") || s.contains("화이트") || s.contains("흰") || s.contains("아이보리화이트")) return "흰색 계열";

        return "기타";
    }

    //연료
    private static final List<String> FUEL_ORDER = List.of(
            "가솔린","디젤","LPG(일반인 구입)","가솔린+전기","LPG+전기","가솔린+LPG","가솔린+CNG","전기","수소","CNG","기타"
    );
    private static final Set<String> KNOWN_FUELS = new HashSet<>(FUEL_ORDER);

    public LinkedHashMap<String, Long> fuelTypeCountsZero() {
        Map<String, Long> tmp = new HashMap<>();
        long etc = 0;
        for (var r : repo.countByFuelType()) {
            String v = safe(r.getVal());
            if (KNOWN_FUELS.contains(v)) tmp.merge(v, r.getCnt(), Long::sum);
            else etc += r.getCnt(); // 기타로 합산
        }
        LinkedHashMap<String, Long> out = new LinkedHashMap<>();
        for (String k : FUEL_ORDER) {
            out.put(k, "기타".equals(k) ? etc : tmp.getOrDefault(k, 0L));
        }
        return out;
    }

    //변속기
    private static final List<String> TRANS_ORDER = List.of("오토","수동","세미오토","CVT","기타");
    private static final Set<String> KNOWN_TRANS = new HashSet<>(TRANS_ORDER);

    public LinkedHashMap<String, Long> transmissionCountsZero() {
        Map<String, Long> tmp = new HashMap<>();
        long etc = 0;
        for (var r : repo.countByTransmission()) {
            String v = safe(r.getVal());
            if (KNOWN_TRANS.contains(v)) tmp.merge(v, r.getCnt(), Long::sum);
            else etc += r.getCnt();
        }
        LinkedHashMap<String, Long> out = new LinkedHashMap<>();
        for (String k : TRANS_ORDER) {
            out.put(k, "기타".equals(k) ? etc : tmp.getOrDefault(k, 0L));
        }
        return out;
    }
    private String safe(String s){ return s==null? "" : s.trim(); }


    private LinkedHashMap<String, Long> zeroFill(List<String> order, List<CarSaleRepository.FacetAgg> rows) {
        Map<String, Long> tmp = new HashMap<>();
        for (var r : rows) tmp.put(r.getVal(), r.getCnt());
        LinkedHashMap<String, Long> res = new LinkedHashMap<>();
        for (String k : order) res.put(k, tmp.getOrDefault(k, 0L));
        return res;
    }
}

