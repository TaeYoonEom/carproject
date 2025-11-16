package com.example.carproject.service;

import com.example.carproject.domain.CarConditionHistory;
import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.repository.CarConditionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final CarConditionHistoryRepository conditionRepo;

    /** 드래프트의 상태/이력 컬럼을 car_condition_history에 upsert */
    @Transactional
    public void upsertFromDraft(Integer carId, CarEntryDraft d) {
        CarConditionHistory ch = conditionRepo.findByCarId(carId)
                .orElseGet(() -> {
                    CarConditionHistory n = new CarConditionHistory();
                    n.setCarId(carId);
                    n.setDraftId(d.getId());
                    return n;
                });

        // 정수류(널 방어는 0)
        ch.setTirePercentage(nzi(d.getTirePercentage()));
        ch.setAccidentRepairCnt(nzi(d.getAccidentRepairCnt()));
        ch.setTotalLossCnt(nzi(d.getTotalLossCnt()));
        ch.setFloodCnt(nzi(d.getFloodCnt()));
        ch.setPanelReplacementCnt(nzi(d.getPanelReplacementCnt()));
        ch.setInsuranceClaimCost(nzi(d.getInsuranceClaimCost()));

        // 불린류(널 방어는 false)
        ch.setEngineOilIssue(nzb(d.getEngineOilIssue()));
        ch.setBrakeIssue(nzb(d.getBrakeIssue()));
        ch.setPerformanceChecked(nzb(d.getPerformanceChecked()));
        ch.setThirdPartyDamage(nzb(d.getThirdPartyDamage()));
        ch.setPanelBeating(nzb(d.getPanelBeating()));
        ch.setReplacementMinor(nzb(d.getReplacementMinor()));
        ch.setCorrosion(nzb(d.getCorrosion()));

        // 텍스트
        ch.setSpecialNote(d.getSpecialNote());

        conditionRepo.save(ch);
    }

    private int  nzi(Integer v){ return v == null ? 0 : v; }
    private boolean nzb(Boolean v){ return v != null && v; }
}
