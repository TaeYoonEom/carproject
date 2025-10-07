package com.example.carproject.buy.service;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.repository.CarSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KoreanFilterService {

    private final CarSaleRepository repo;

    // 코드 → 한글명 매핑
    private static final Map<String, String> CODE_TO_KO = Map.ofEntries(
            Map.entry("KE","경차"), Map.entry("SM","소형차"), Map.entry("JM","준중형차"),
            Map.entry("MD","중형차"), Map.entry("LG","대형차"), Map.entry("SC","스포츠카"),
            Map.entry("SUV","SUV"), Map.entry("RV","RV"), Map.entry("VN","승합차"),
            Map.entry("TR","화물차"), Map.entry("ET","기타")
    );
    private static final Set<String> KNOWN_KO_TYPES =
            new LinkedHashSet<>(CODE_TO_KO.values()); // {"경차","소형차",...,"기타"}

    /** 차종별 카운트(코드 키). 매핑 없는 값과 null은 ET에 합산 */
    public Map<String, Long> getTypeCountsByCode() {
        var raw = repo.countByCarType(); // 한글/NULL 기준
        long others = 0L;
        Map<String, Long> byKo = new HashMap<>();

        for (var r : raw) {
            String ko = r.getCarType(); // may be null
            long cnt = r.getCnt();
            if (ko == null || !KNOWN_KO_TYPES.contains(ko)) {
                others += cnt; // ✅ 기타로 누적
            } else {
                byKo.merge(ko, cnt, Long::sum);
            }
        }

        Map<String, Long> result = new LinkedHashMap<>();
        // 모든 코드 키를 채워줌(없으면 0)
        for (var e : CODE_TO_KO.entrySet()) {
            String code = e.getKey();
            String ko   = e.getValue();
            long cnt = "기타".equals(ko) ? others : byKo.getOrDefault(ko, 0L);
            result.put(code, cnt);
        }
        return result;
    }

    /** 선택 코드 기준 검색. ET가 포함되면 '기타'까지 합집합으로 반환 */
    public List<CarSale> findByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) return repo.findAll();

        boolean includeET = codes.contains("ET");
        // 선택된 코드 중 실제 매핑되는 한글만 취함(안전)
        List<String> selectedKo = codes.stream()
                .map(CODE_TO_KO::get)        // code -> ko (null 가능)
                .filter(Objects::nonNull)
                .filter(ko -> !"기타".equals(ko))
                .toList();

        List<CarSale> result = new ArrayList<>();
        if (!selectedKo.isEmpty()) result.addAll(repo.findByCarTypeIn(selectedKo));
        if (includeET)             result.addAll(repo.findOthers(KNOWN_KO_TYPES));

        // ✅ carId 기준 중복 제거(합집합)
        Map<Integer, CarSale> uniq = new LinkedHashMap<>();
        for (var c : result) uniq.put(c.getCarId(), c);
        return new ArrayList<>(uniq.values());
    }

}

