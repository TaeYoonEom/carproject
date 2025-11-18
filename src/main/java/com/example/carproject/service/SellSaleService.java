package com.example.carproject.service;

import com.example.carproject.domain.AllCarSale;
import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.repository.AllCarSaleRepository2;
import com.example.carproject.repository.CarEntryDraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellSaleService {

    private final CarEntryDraftRepository draftRepo;
    private final AllCarSaleRepository2 allRepo;
    private final ConditionService conditionService;

    @Transactional
    public Integer saveSaleInfoAndUpsertCondition(
            Integer draftId,
            String deliveryOption,
            String carGrade,
            String saleType,
            String saleMethod
    ) {
        CarEntryDraft draft = draftRepo.findById(draftId)
                .orElseThrow(() -> new IllegalArgumentException("Draft not found: " + draftId));

        // 1) 판매정보 저장
        draft.setDeliveryOption(deliveryOption);
        draft.setCarGrade(carGrade);
        draft.setSaleType(saleType);
        draft.setSaleMethod(saleMethod);
        draftRepo.save(draft);

        // 2) all_car_sale 보장(없으면 생성) + draft.carId 역기입
        Integer carId = draft.getCarId();
        if (carId == null || carId == 0) {
            AllCarSale all = allRepo.findByCarEntryDraftId(draft.getId())
                    .orElseGet(() -> createAllCarSaleFromDraft(draft));
            carId = all.getCarId();
            draft.setCarId(carId);
            draftRepo.save(draft);
        }

        // 3) 컨디션 히스토리 업서트
        conditionService.upsertFromDraft(carId, draft);

        return carId;
    }

    private AllCarSale createAllCarSaleFromDraft(CarEntryDraft d) {
        AllCarSale a = new AllCarSale();
        a.setMemberId(d.getMemberId());
        a.setOrigin(d.getOrigin() == null ? 0 : d.getOrigin());
        a.setIsEcoFriendly(Boolean.TRUE.equals(d.getIsEcoFriendly()));

        // 🔥 cargo boolean → int 변환
        int cargoValue = (d.getCarType() != null && d.getCarType().contains("화물")) ? 1 : 0;
        a.setIsCargo(cargoValue);

        a.setCarEntryDraftId(d.getId());
        return allRepo.save(a);
    }

}
