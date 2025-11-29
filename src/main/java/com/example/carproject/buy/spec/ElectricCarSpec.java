// src/main/java/com/example/carproject/buy/spec/ElectricCarSpec.java
package com.example.carproject.buy.spec;

import com.example.carproject.buy.domain.EcoCar;
import com.example.carproject.buy.dto.ElectricFilterRequest;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
public class ElectricCarSpec {

    public static Specification<EcoCar> build(ElectricFilterRequest req) {

        return (root, query, cb) -> {

            List<Predicate> p = new ArrayList<>();
            if (req == null) return cb.and(p.toArray(new Predicate[0]));

            // ✅ 리스트 필터들
            if (has(req.getCarType()))
                p.add(root.get("carType").in(req.getCarType()));

            if (has(req.getManufacturer()))
                p.add(root.get("manufacturer").in(req.getManufacturer()));

            if (has(req.getModelName()))
                p.add(root.get("modelName").in(req.getModelName()));

            if (has(req.getCarName()))
                p.add(root.get("carName").in(req.getCarName()));

            if (has(req.getFuelType()))
                p.add(root.get("fuelType").in(req.getFuelType()));

            if (has(req.getTransmission()))
                p.add(root.get("transmission").in(req.getTransmission()));

            if (has(req.getSaleLocation()))
                p.add(root.get("saleLocation").in(req.getSaleLocation()));

            if (has(req.getSellerType()))
                p.add(root.get("sellerType").in(req.getSellerType()));

            if (has(req.getSaleMethod()))
                p.add(root.get("saleMethod").in(req.getSaleMethod()));

            if (has(req.getExteriorColor()))
                p.add(root.get("exteriorColor").in(req.getExteriorColor()));

            if (has(req.getInteriorColor()))
                p.add(root.get("interiorColor").in(req.getInteriorColor()));

            if (has(req.getPerformanceOpen()))
                p.add(root.get("performanceOpen").in(req.getPerformanceOpen()));

            // ✅ 인승 (bucket → 실제 값)
            if (has(req.getCapacity())) {
                List<Predicate> caps = new ArrayList<>();
                for (String c : req.getCapacity()) {
                    if ("LE2".equals(c))
                        caps.add(cb.le(root.get("capacity"), 2));
                    else if ("GE10".equals(c))
                        caps.add(cb.ge(root.get("capacity"), 10));
                    else
                        caps.add(cb.equal(root.get("capacity"), Integer.valueOf(c.substring(2))));
                }
                p.add(cb.or(caps.toArray(new Predicate[0])));
            }

            // ✅ 가격
            if (req.getPriceMin() != null)
                p.add(cb.ge(root.get("price"), req.getPriceMin() * 10000));

            if (req.getPriceMax() != null)
                p.add(cb.le(root.get("price"), req.getPriceMax() * 10000));

            // ✅ 연식 (year + month)
            if (req.getYearFrom() != null && req.getMonthFrom() != null) {
                p.add(cb.or(
                        cb.gt(root.get("year"), req.getYearFrom()),
                        cb.and(
                                cb.equal(root.get("year"), req.getYearFrom()),
                                cb.ge(root.get("month"), req.getMonthFrom())
                        )
                ));
            }

            if (req.getYearTo() != null && req.getMonthTo() != null) {
                p.add(cb.or(
                        cb.lt(root.get("year"), req.getYearTo()),
                        cb.and(
                                cb.equal(root.get("year"), req.getYearTo()),
                                cb.le(root.get("month"), req.getMonthTo())
                        )
                ));
            }

            // ✅ 주행거리
            if (req.getMileageMin() != null)
                p.add(cb.ge(root.get("mileage"), req.getMileageMin()));

            if (req.getMileageMax() != null)
                p.add(cb.le(root.get("mileage"), req.getMileageMax()));

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private static boolean has(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
