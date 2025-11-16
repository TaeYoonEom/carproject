package com.example.carproject.service;

import com.example.carproject.domain.AllCarSale;
import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.domain.CarSold;
import com.example.carproject.repository.AllCarSaleRepository2;
import com.example.carproject.repository.CarEntryDraftRepository;
import com.example.carproject.repository.CarSoldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellSubmissionService {

    private final CarEntryDraftRepository draftRepo;   // <-- JpaRepository<CarEntryDraft, Integer>
    private final AllCarSaleRepository2 allRepo;
    private final CarSoldRepository soldRepo;

    // ✅ 추가된 부분
    private final CarPublishService carPublishService;
    private final ConditionService conditionService;

    /**
     * 초안 제출(내차등록 완료) 시 호출:
     * 1) all_car_sale 생성/재사용
     * 2) draft.car_id 역기입
     * 3) car_sold('판매중') 생성/유지
     * 4) car_sale / import_car_sale / car_image 자동 생성 (CarPublishService 호출)
     * @return 생성/연결된 carId
     */
    @Transactional
    public int processDraftSubmission(Integer draftId, Integer memberId) {
        // 0️⃣ 초안 로딩 + 소유자 검증
        CarEntryDraft draft = draftRepo.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Draft not found: " + draftId));

        if (draft.getMemberId() == null || !draft.getMemberId().equals(memberId)) {
            throw new SecurityException("본인 초안만 제출할 수 있습니다.");
        }

        // 제출 플래그가 숫자/불린 혼용될 수 있으니 안전 체크
        if (!isSubmittedTrue(draft)) {
            // draft.setIsSubmitted(true);
            // draftRepo.save(draft);
        }

        // 1️⃣ all_car_sale upsert (car_entry_draft_id UNIQUE 기준 멱등)
        AllCarSale all = allRepo.findByCarEntryDraftId(draft.getId())
                .orElseGet(() -> createAllCarSaleFromDraft(draft));

        // 2️⃣ draft.car_id 역기입 (멱등)
        if (draft.getCarId() == null || draft.getCarId() == 0) {
            draft.setCarId(all.getCarId());
            draftRepo.save(draft);
        }

        // 3️⃣ car_sold 생성/유지 (멱등)
        if (soldRepo.findByCarId(all.getCarId()).isEmpty()) {
            CarSold sold = new CarSold();
            sold.setMemberId(memberId);
            sold.setCarId(all.getCarId());
            sold.setStatus(CarSold.Status.판매중);
            soldRepo.save(sold);
        }

        // ✅ 4️⃣ car_publish 자동 호출
        try {
            carPublishService.publishFromDraft(draft.getId());
            conditionService.upsertFromDraft(all.getCarId(), draft);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("CarPublishService 호출 중 오류 발생: " + e.getMessage());
        }

        return all.getCarId();
    }

    /**
     * Draft를 기반으로 AllCarSale 생성 (중복 안전)
     */
    private AllCarSale createAllCarSaleFromDraft(CarEntryDraft d) {
        AllCarSale a = new AllCarSale();
        a.setMemberId(d.getMemberId());

        // ✅ origin: draft의 0/1 값을 그대로 저장 (0=국산, 1=수입)
        Integer originValue = (d.getOrigin() != null) ? d.getOrigin() : 0; // null 방어
        a.setOrigin(originValue);

        // ✅ eco: 드래프트 isEcoFriendly 우선, 없으면 연료 타입으로 추론
        Boolean eco = d.getIsEcoFriendly();
        if (eco == null) {
            String fuel = d.getFuelType();
            eco = (fuel != null && (fuel.contains("전기") || fuel.contains("수소")));
        }
        a.setIsEcoFriendly(eco);

        // ✅ cargo: 기본 false 처리
        a.setIsCargo(d.getCarType() != null && d.getCarType().contains("화물"));

        // ✅ draft 연결
        a.setCarEntryDraftId(d.getId());

        try {
            return allRepo.save(a);
        } catch (DataIntegrityViolationException e) {
            // UNIQUE(car_entry_draft_id) 충돌 시 이미 생성된 행 재조회
            return allRepo.findByCarEntryDraftId(d.getId())
                    .orElseThrow(() -> e);
        }
    }

    /**
     * isSubmitted이 Integer(0/1) 또는 Boolean일 수 있는 경우 모두 true 판정
     */
    private boolean isSubmittedTrue(CarEntryDraft d) {
        Object v = d.getIsSubmitted();
        if (v == null) return false;
        if (v instanceof Boolean b) return b;
        if (v instanceof Number n) return n.intValue() == 1;
        if (v instanceof String s) return "1".equals(s) || "true".equalsIgnoreCase(s);
        return false;
    }
}
