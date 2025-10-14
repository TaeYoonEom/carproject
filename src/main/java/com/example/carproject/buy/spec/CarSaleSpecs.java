package com.example.carproject.buy.spec;

import com.example.carproject.buy.domain.CarSale;
import com.example.carproject.buy.dto.FilterRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CarSaleSpecs {
    private static final Set<String> KNOWN_CARTYPES = Set.of(
            "경차","소형차","준중형차","중형차","대형차","스포츠카",
            "SUV","RV","경승합차","승합차","화물차" // '기타' 제외
    );

    // 페이지에서 top7로 쓴 리스트와 일치시키는 게 베스트
    private static final Set<String> TOP_MANUFACTURERS = Set.of(
            "현대","제네시스","기아","쉐보레(GM대우)","르노코리아(삼성)","KG모빌리티(쌍용)","혼다" // 예시, 네 데이터에 맞춰 수정
    );
    //  fuelType, transmission 고정값 (기타 처리용)
    private static final Set<String> KNOWN_FUELS = Set.of(
            "가솔린", "디젤", "LPG(일반인 구입)", "가솔린+전기", "LPG+전기", "가솔린+LPG", "가솔린+CNG", "전기", "수소", "CNG"
    );
    private static final Set<String> KNOWN_TRANS = Set.of(
            "오토", "수동", "세미오토", "CVT"
    );

    public static Specification<CarSale> from(FilterRequest f) {
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (notEmpty(f.getManufacturers())) ps.add(root.get("manufacturer").in(f.getManufacturers()));
            if (notEmpty(f.getModelNames())) ps.add(root.get("modelName").in(f.getModelNames()));
            if (notEmpty(f.getCarNames())) ps.add(root.get("carName").in(f.getCarNames()));
            if (notEmpty(f.getPerformanceOpen())) ps.add(root.get("performanceOpen").in(f.getPerformanceOpen()));
            if (notEmpty(f.getFuelTypes())) ps.add(root.get("fuelType").in(f.getFuelTypes()));
            if (notEmpty(f.getTransmissions())) ps.add(root.get("transmission").in(f.getTransmissions()));
            if (notEmpty(f.getSellerTypes())) ps.add(root.get("sellerType").in(f.getSellerTypes()));
            if (notEmpty(f.getSaleMethods())) ps.add(root.get("saleMethod").in(f.getSaleMethods()));
            if (notEmpty(f.getCarTypes())) ps.add(root.get("carType").in(f.getCarTypes()));
            if (notEmpty(f.getSaleLocations())) ps.add(root.get("saleLocation").in(f.getSaleLocations()));
            if (notEmpty(f.getExteriorColors())) ps.add(root.get("exteriorColor").in(f.getExteriorColors()));
            if (notEmpty(f.getInteriorColors())) ps.add(root.get("interiorColor").in(f.getInteriorColors()));
            if (notEmpty(f.getSeatColors())) ps.add(root.get("seatColor").in(f.getSeatColors()));
            if (notEmpty(f.getCapacities())) ps.add(root.get("capacity").in(f.getCapacities()));

            // ===== '기타' 처리 포함 IN 조건 =====
            addInWithEtc(ps, root.get("fuelType"),
                    f.getFuelTypes(), KNOWN_FUELS, "기타", cb);

            addInWithEtc(ps, root.get("transmission"),
                    f.getTransmissions(), KNOWN_TRANS, "기타", cb);

            addInWithEtc(ps, root.get("carType"),
                    f.getCarTypes(), KNOWN_CARTYPES, "기타", cb);

            addInWithEtc(ps, root.get("manufacturer"),
                    f.getManufacturers(), TOP_MANUFACTURERS, "기타 제조사", cb);

            // 범위조건
            if (f.getPriceMin()!=null) ps.add(cb.greaterThanOrEqualTo(root.get("price"), f.getPriceMin()));
            if (f.getPriceMax()!=null) ps.add(cb.lessThanOrEqualTo(root.get("price"), f.getPriceMax()));
            if (f.getYearFrom()!=null) ps.add(cb.greaterThanOrEqualTo(root.get("year"), f.getYearFrom()));
            if (f.getYearTo()!=null) ps.add(cb.lessThanOrEqualTo(root.get("year"), f.getYearTo()));
            if (f.getMileageMin()!=null) ps.add(cb.greaterThanOrEqualTo(root.get("mileage"), f.getMileageMin()));
            if (f.getMileageMax()!=null) ps.add(cb.lessThanOrEqualTo(root.get("mileage"), f.getMileageMax()));

            return ps.isEmpty()? cb.conjunction() : cb.and(ps.toArray(new Predicate[0]));
        };
    }
    // ✅ '기타' 포함 IN 헬퍼 (단 하나만 선언)
    private static void addInWithEtc(List<Predicate> ps, Path<String> path,
                                     List<String> selected, Set<String> knownValues,
                                     String etcLabel, CriteriaBuilder cb) {
        if (selected == null || selected.isEmpty()) return;

        boolean hasEtc = selected.contains(etcLabel);
        List<String> normalValues = new ArrayList<>(selected);
        normalValues.remove(etcLabel);

        List<Predicate> orList = new ArrayList<>();
        if (!normalValues.isEmpty()) orList.add(path.in(normalValues));
        if (hasEtc) orList.add(cb.or(cb.isNull(path), cb.not(path.in(knownValues))));

        if (!orList.isEmpty()) ps.add(cb.or(orList.toArray(new Predicate[0])));
    }

    private static boolean notEmpty(List<?> v) { return v != null && !v.isEmpty(); }
}
