package com.example.carproject.controller;

import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.domain.CarConditionHistory;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.CarEntryDraftRepository;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.repository.CarConditionHistoryRepository;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.io.File;
import java.io.IOException;

// ✅ 추가된 import
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
public class SellCarController {

    private final MemberRepository memberRepository;
    private final CarEntryDraftRepository carEntryDraftRepository;

    // 1️⃣ 판매 방식 선택 메인 페이지
    @GetMapping("/sell")
    public String showSellPage() {
        return "sell_cars";
    }

    // 2️⃣ 빠른 판매 페이지
    @GetMapping("/sell/quick")
    public String quickSell() {
        return "sell_quick";
    }

    // 3️⃣ 직접 판매 페이지
    @GetMapping("/sell/direct")
    public String directSell() {
        return "sell_direct";
    }

    // 4️⃣ 소유자명 입력 페이지
    @GetMapping("/sell/detail/owner")
    public String inputOwnerName(@RequestParam String carNumber, Model model) {
        model.addAttribute("carNumber", carNumber);
        return "selldetail/owner_name";
    }

    // 5️⃣ 차량번호 + 소유자명 저장 → 차량 상세 입력 페이지로 이동
    @GetMapping("/sell/detail/input")
    public String inputCarInfo(@RequestParam String carNumber,
                               @RequestParam String owner,
                               Principal principal,
                               Model model) {
        String loginId = principal.getName();
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        Long memberId = member.getMemberId().longValue();

        Optional<CarEntryDraft> optionalDraft =
                carEntryDraftRepository.findByMemberIdAndCarNumber(memberId, carNumber);

        CarEntryDraft draft = optionalDraft.orElseGet(CarEntryDraft::new);
        draft.setMemberId(member.getMemberId());
        draft.setCarNumber(carNumber);
        draft.setOwnerName(owner);
        draft.setIsSubmitted(false);

        if (draft.getId() == null) {
            draft.setCreatedAt(LocalDateTime.now());
        }

        carEntryDraftRepository.save(draft);

        model.addAttribute("draft", draft);
        return "selldetail/sell_input";
    }

    // ✅ 6️⃣ 차량 상세 정보 저장 처리
    @PostMapping("/sell/detail/input/save")
    public String saveCarDetails(@RequestParam String carNumber,
                                 @RequestParam String ownerName,
                                 @RequestParam String modelName,
                                 @RequestParam String manufactureDate, // "YYYY-MM" 형식
                                 @RequestParam Integer mileage,
                                 @RequestParam String region,
                                 Principal principal) {

        String loginId = principal.getName();
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        Long memberId = member.getMemberId().longValue();

        CarEntryDraft draft = carEntryDraftRepository
                .findByMemberIdAndCarNumber(memberId, carNumber)
                .orElse(new CarEntryDraft());

        draft.setMemberId(member.getMemberId());
        draft.setCarNumber(carNumber);
        draft.setOwnerName(ownerName);
        draft.setModelName(modelName);
        draft.setMileage(mileage);
        draft.setRegion(region);
        draft.setManufactureDate(LocalDate.parse(manufactureDate + "-01")); // "YYYY-MM" → LocalDate
        draft.setIsSubmitted(true);

        if (draft.getId() == null) {
            draft.setCreatedAt(LocalDateTime.now());
        }

        carEntryDraftRepository.save(draft);

        return "redirect:/sell/detail/view?carNumber=" + URLEncoder.encode(carNumber, StandardCharsets.UTF_8);
    }

    @GetMapping("/sell/detail/view")
    public String viewCarDetail(@RequestParam String carNumber, Model model) {
        CarEntryDraft car = carEntryDraftRepository.findByCarNumber(carNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 정보를 찾을 수 없습니다."));

        // 간단 시세 예측 로직
        int yearsOld = Period.between(car.getManufactureDate(), LocalDate.now()).getYears();
        int basePrice = 2000; // 만원 단위
        int mileage = car.getMileage() != null ? car.getMileage() : 0;

        // 예시 공식
        int estimated = basePrice - (yearsOld * 100) - (mileage / 20000 * 50);
        estimated = Math.max(estimated, 300); // 최저 보장

        // 범위 제공: ±15% 오차
        int minPrice = (int) Math.floor(estimated * 0.7 / 10) * 10;
        int maxPrice = (int) Math.ceil(estimated * 1.1 / 10) * 10;

        model.addAttribute("car", car);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("estimatedPrice", estimated);
        return "selldetail/car_view";
    }

    @GetMapping("/sell/detail/photo")
    public String showPhotoUpload(@RequestParam("carId") Long carId, Model model) {
        CarEntryDraft car = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("차량 정보가 없습니다."));
        model.addAttribute("car", car);
        model.addAttribute("cb", System.currentTimeMillis()); // ✅ 캐시버스터 값
        return "selldetail/sell_photo";
    }

    @PostMapping("/sell/detail/photo/upload")
    public String uploadCarPhotos(@RequestParam("carId") Long carId,
                                  @RequestParam("front") MultipartFile front,
                                  @RequestParam("left") MultipartFile left,
                                  @RequestParam("right") MultipartFile right,
                                  @RequestParam("rear") MultipartFile rear,
                                  @RequestParam("driver") MultipartFile driver,
                                  @RequestParam("back") MultipartFile back) throws IOException {

        // 1. 파일 저장 경로
        String basePath = System.getProperty("user.dir") +
                "/src/main/resources/static/img/entry_car_img/";

        // 2. 파일명 규칙
        String[] positions = {"front", "left", "right", "rear", "driver", "back"};
        MultipartFile[] files = {front, left, right, rear, driver, back};
        String[] dbUrls = new String[6];

        for (int i = 0; i < positions.length; i++) {
            MultipartFile file = files[i];
            if (file != null && !file.isEmpty()) {
                String fileName = String.format("entry_car_%d_%s.jpg", carId, positions[i]);
                File dest = new File(basePath + fileName);
                dest.getParentFile().mkdirs();
                file.transferTo(dest);
                dbUrls[i] = "/img/entry_car_img/" + fileName;
            }
        }

        // 3. DB에 URL 저장
        CarEntryDraft draft = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("차량 정보를 찾을 수 없습니다."));
        draft.setFrontViewUrl(dbUrls[0]);
        draft.setLeftSideUrl(dbUrls[1]);
        draft.setRightSideUrl(dbUrls[2]);
        draft.setRearViewUrl(dbUrls[3]);
        draft.setDriverSeatUrl(dbUrls[4]);
        draft.setBackSeatUrl(dbUrls[5]);
        carEntryDraftRepository.save(draft);

        // 다음 페이지로 이동 (예시: 색상 선택)
        return "redirect:/sell/detail/color?carId=" + carId;
    }

    @GetMapping("/sell/detail/photo/preview")
    public String previewCarPhotos(@RequestParam("carId") Long carId, Model model) {
        CarEntryDraft car = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 정보가 없습니다."));
        model.addAttribute("car", car);
        return "selldetail/sell_photo_preview";
    }

    @GetMapping("/sell/detail/color")
    public String showColorForm(@RequestParam("carId") Long carId, Model model) {
        CarEntryDraft carEntryDraft = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 정보가 없습니다."));
        model.addAttribute("color", carEntryDraft);   // 색상 포함 draft 전체 전달
        model.addAttribute("carId", carId);           // id도 같이 전달
        return "selldetail/sell_color";
    }

    // 색상 저장 처리
    @PostMapping("/sell/detail/color/save")
    public String saveCarColor(@RequestParam Long carId,
                               @RequestParam String exteriorColor,
                               @RequestParam String interiorColor,
                               @RequestParam String seatColor) {
        CarEntryDraft car = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("차량 정보를 찾을 수 없습니다."));
        car.setExteriorColor(exteriorColor);
        car.setInteriorColor(interiorColor);
        car.setSeatColor(seatColor);
        carEntryDraftRepository.save(car);

        // 다음 단계로 이동 (ex. 옵션 선택 페이지)
        return "redirect:/sell/detail/option?carId=" + carId;
    }

    @PostMapping("/sell/detail/option/save")
    public String saveOption(
            @RequestParam("carId") Long carId,
            @RequestParam("driveType") String driveType,
            @RequestParam("carType") String carType,
            @RequestParam("fuelType") String fuelType,
            @RequestParam("transmission") String transmission,
            @RequestParam(value = "isEcoFriendly", required = false, defaultValue = "0") int isEcoFriendly
    ) {
        CarEntryDraft draft = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 정보가 없습니다."));
        draft.setDriveType(driveType);
        draft.setCarType(carType);
        draft.setFuelType(fuelType);
        draft.setTransmission(transmission);
        draft.setIsEcoFriendly(isEcoFriendly == 1);
        carEntryDraftRepository.save(draft);

        return "redirect:/sell/detail/condition?carId=" + carId; // 다음 페이지로 이동
    }

    private final CarConditionHistoryRepository carConditionHistoryRepository;
    @GetMapping("/sell/detail/condition")
    public String showConditionForm(@RequestParam("carId") Long carId, Model model) {
        CarConditionHistory exist = carConditionHistoryRepository.findById(carId.intValue()).orElse(null);
        model.addAttribute("carId", carId);
        model.addAttribute("condition", exist);
        return "selldetail/sell_condition";
    }

    @PostMapping("/sell/detail/condition/save")
    public String saveCondition(
            @RequestParam("carId") Long carId,
            @RequestParam(value="tirePercentage", defaultValue="0") Integer tirePercentage,
            @RequestParam(value="engineOilIssue", defaultValue="0") Integer engineOilIssue,
            @RequestParam(value="brakeIssue", defaultValue="0") Integer brakeIssue,
            @RequestParam(value="performanceChecked", defaultValue="1") Integer performanceChecked,
            @RequestParam(value="accidentRepairCnt", defaultValue="0") Integer accidentRepairCnt,
            @RequestParam(value="totalLossCnt", defaultValue="0") Integer totalLossCnt,
            @RequestParam(value="floodCnt", defaultValue="0") Integer floodCnt,
            @RequestParam(value="panelReplacementCnt", defaultValue="0") Integer panelReplacementCnt,
            @RequestParam(value="insuranceClaimCost", defaultValue="0") Integer insuranceClaimCost,
            @RequestParam(value="thirdPartyDamage", defaultValue="0") Integer thirdPartyDamage,
            @RequestParam(value="panelBeating", defaultValue="0") Integer panelBeating,
            @RequestParam(value="replacementMinor", defaultValue="0") Integer replacementMinor,
            @RequestParam(value="corrosion", defaultValue="0") Integer corrosion,
            @RequestParam(value="specialNote", required=false) String specialNote
    ){
        CarConditionHistory entity = carConditionHistoryRepository.findById(carId.intValue())
                .orElseGet(() -> CarConditionHistory.builder().carId(carId.intValue()).build());

        entity.setTirePercentage(tirePercentage);
        entity.setEngineOilIssue(engineOilIssue == 1);
        entity.setBrakeIssue(brakeIssue == 1);
        entity.setPerformanceChecked(performanceChecked == 1);
        entity.setAccidentRepairCnt(accidentRepairCnt);
        entity.setTotalLossCnt(totalLossCnt);
        entity.setFloodCnt(floodCnt);
        entity.setPanelReplacementCnt(panelReplacementCnt);
        entity.setInsuranceClaimCost(insuranceClaimCost);
        entity.setThirdPartyDamage(thirdPartyDamage == 1);
        entity.setPanelBeating(panelBeating == 1);
        entity.setReplacementMinor(replacementMinor == 1);
        entity.setCorrosion(corrosion == 1);
        entity.setSpecialNote(specialNote);

        carConditionHistoryRepository.save(entity);

        // ✅ 검사/이력 완료 → 판매정보로 이동
        return "redirect:/sell/detail/sale?carId=" + carId;
    }






    @GetMapping("/sell/detail/option")
    public String showOptionForm(@RequestParam("carId") Long carId, Model model) {
        CarEntryDraft carEntryDraft = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 정보가 없습니다."));
        model.addAttribute("option", carEntryDraft);
        model.addAttribute("carId", carId);
        return "selldetail/sell_option";
    }

    @GetMapping("/sell/detail/sale")
    public String showSaleForm(@RequestParam("carId") Long carId, Model model) {
        CarEntryDraft carEntryDraft = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 정보가 없습니다."));
        model.addAttribute("carId", carId);
        return "selldetail/sell_sale";
    }

    @PostMapping("/sell/detail/sale/save")
    public String saveSaleInfo(
            @RequestParam("carId") Long carId,
            @RequestParam("deliveryOption") String deliveryOption,
            @RequestParam("carGrade") String carGrade,
            @RequestParam("saleType") String saleType,
            @RequestParam("saleMethod") String saleMethod
    ) {
        CarEntryDraft draft = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("차량 정보 없음"));
        draft.setDeliveryOption(deliveryOption);
        draft.setCarGrade(carGrade);
        draft.setSaleType(saleType);
        draft.setSaleMethod(saleMethod);
        carEntryDraftRepository.save(draft);

        // 다음 단계 또는 완료 페이지로 리다이렉트
        return "redirect:/sell/detail/confirm?carId=" + carId;
    }

    @GetMapping("/sell/detail/confirm")
    public String confirmCarEntry(@RequestParam("carId") Long carId, Model model) {
        CarEntryDraft car = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("차량 정보를 찾을 수 없습니다."));
        model.addAttribute("car", car);
        model.addAttribute("cb", System.currentTimeMillis()); // ✅
        return "selldetail/car_confirm";
    }

    // ✅ 팝업 종료 + 부모창 알림/갱신 (팝업이 아닐 땐 기존처럼 알림 후 홈 이동)
    @GetMapping("/sell/detail/complete")
    public ResponseEntity<String> completeAndAlert(@RequestParam("carId") Long carId,
                                                   Principal principal) {
        try {
            CarEntryDraft draft = carEntryDraftRepository.findById(carId)
                    .orElseThrow(() -> new IllegalArgumentException("차량 정보를 찾을 수 없습니다."));
            draft.setIsSubmitted(true);
            carEntryDraftRepository.save(draft);

            String body =
                    "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body><script>" +
                            "var msg='차량 등록이 완료되었습니다.';" +
                            "(function(){ " +
                            "  var notified=false;" +
                            "  if(window.opener && !window.opener.closed){" +
                            "    try{ window.opener.focus(); }catch(e){}" +
                            "    try{ if(typeof window.opener.showRegisterDone==='function'){ window.opener.showRegisterDone(msg); notified=true; } }catch(e){}" +
                            "    if(!notified){ try{ window.opener.alert(msg); notified=true; }catch(e){} }" +
                            "    if(!notified){ alert(msg); }" +  // ✅ 마지막 보루: 팝업에서라도 알림 띄우기
                            "    setTimeout(function(){ " +
                            "      try{ window.close(); }catch(e){} " +
                            "      try{ window.open('','_self'); window.close(); }catch(e){} " +
                            "    }, 150);" + // ✅ 너무 빨리 닫히지 않도록 약간의 딜레이
                            "  } else {" +
                            "    alert(msg);" +
                            "    window.location.href='/'" +
                            "  }" +
                            "})();" +
                            "</script></body></html>";

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(body);

        } catch (Exception e) {
            String errorBody =
                    "<!DOCTYPE html><html><head><meta charset='UTF-8'></head><body><script>" +
                            "alert('등록 처리 중 오류가 발생했습니다. 다시 시도해주세요.');" +
                            "if(window.opener && !window.opener.closed){" +
                            "  setTimeout(function(){ try{ window.close(); }catch(e){}; try{ window.open('','_self'); window.close(); }catch(e){}; }, 150);" +
                            "} else { history.back(); }" +
                            "</script></body></html>";
            return ResponseEntity.status(400)
                    .header("Content-Type", "text/html; charset=UTF-8")
                    .body(errorBody);
        }
    }
}
