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

    public static Specification<CarSale> from(FilterRequest f, Set<String> topManufacturers) {
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
            addInWithEtc(ps, root.get("fuelType"), f.getFuelTypes(),
                    Set.of("가솔린","디젤","LPG(일반인 구입)","가솔린+전기","LPG+전기","가솔린+LPG","가솔린+CNG","전기","수소","CNG"),
                    "기타", cb);

            addInWithEtc(ps, root.get("transmission"), f.getTransmissions(),
                    Set.of("오토","수동","세미오토","CVT"),
                    "기타", cb);

            addInWithEtc(ps, root.get("carType"), f.getCarTypes(),
                    Set.of("경차","소형차","준중형차","중형차","대형차","스포츠카","SUV","RV","경승합차","승합차","화물차"),
                    "기타", cb);

            // 범위조건
            // === 제조사: “기타 제조사” 선택 시 Top Set 이외 모두 포함 ===
            if (notEmpty(f.getManufacturers())) {
                boolean hasEtc = f.getManufacturers().contains("기타 제조사");
                var normals = new ArrayList<>(f.getManufacturers());
                normals.remove("기타 제조사");

                List<Predicate> ors = new ArrayList<>();
                if (!normals.isEmpty()) ors.add(root.get("manufacturer").in(normals));
                if (hasEtc) ors.add(cb.or(cb.isNull(root.get("manufacturer")), cb.not(root.get("manufacturer").in(topManufacturers))));
                if (!ors.isEmpty()) ps.add(cb.or(ors.toArray(new Predicate[0])));
            }

            // === 가격/주행거리 범위 ===
            if (f.getPriceMin()!=null)   ps.add(cb.greaterThanOrEqualTo(root.get("price"),   f.getPriceMin()));
            if (f.getPriceMax()!=null)   ps.add(cb.lessThanOrEqualTo(root.get("price"),     f.getPriceMax()));
            if (f.getMileageMin()!=null) ps.add(cb.greaterThanOrEqualTo(root.get("mileage"), f.getMileageMin()));
            if (f.getMileageMax()!=null) ps.add(cb.lessThanOrEqualTo(root.get("mileage"),   f.getMileageMax()));

            // === 연-월 범위 (둘 다 있을 때만 Month 비교)
            if (f.getYearFrom()!=null) {
                if (f.getMonthFrom()!=null) {
                    ps.add(cb.or(
                            cb.greaterThan(root.get("year"), f.getYearFrom()),
                            cb.and(cb.equal(root.get("year"), f.getYearFrom()),
                                    cb.greaterThanOrEqualTo(root.get("month"), f.getMonthFrom()))
                    ));
                } else {
                    ps.add(cb.greaterThanOrEqualTo(root.get("year"), f.getYearFrom()));
                }
            }
            if (f.getYearTo()!=null) {
                if (f.getMonthTo()!=null) {
                    ps.add(cb.or(
                            cb.lessThan(root.get("year"), f.getYearTo()),
                            cb.and(cb.equal(root.get("year"), f.getYearTo()),
                                    cb.lessThanOrEqualTo(root.get("month"), f.getMonthTo()))
                    ));
                } else {
                    ps.add(cb.lessThanOrEqualTo(root.get("year"), f.getYearTo()));
                }
            }
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
