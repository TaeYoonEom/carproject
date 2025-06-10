package com.example.carproject.controller;

import com.example.carproject.domain.CarEntryDraft;
import com.example.carproject.domain.Member;
import com.example.carproject.repository.CarEntryDraftRepository;
import com.example.carproject.repository.MemberRepository;

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
//    @GetMapping("/sell/detail/view")
//    public String viewCarInfo(@RequestParam String carNumber, Principal principal, Model model) {
//        String loginId = principal.getName();
//        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
//        Long memberId = member.getMemberId().longValue();
//
//        CarEntryDraft draft = carEntryDraftRepository
//                .findByMemberIdAndCarNumber(memberId, carNumber)
//                .orElseThrow();
//
//        model.addAttribute("car", draft);
//        return "selldetail/car_view"; // templates/selldetail/car_view.html
//    }

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

    //    @PostMapping("/sell/detail/photo/confirm")
//    public String confirmPhotos(@RequestParam("carId") Long carId) {
//        // carId로 엔트리 검증 및 다음 단계로 이동
//        return "redirect:/sell/detail/nextstep?carId=" + carId;
//    }
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
        model.addAttribute("color", carEntryDraft);   // <== 색상 포함 draft 전체 전달
        model.addAttribute("carId", carId);           // <== id도 같이 전달
        return "selldetail/sell_color";              // 색상 선택 페이지(Thymeleaf 템플릿) 이름
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

        return "redirect:/sell/detail/sale?carId=" + carId; // 다음 페이지로 이동

    }
    @GetMapping("/sell/detail/option")
    public String showOptionForm(@RequestParam("carId") Long carId, Model model) {
        CarEntryDraft carEntryDraft = carEntryDraftRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("해당 차량 정보가 없습니다."));
        model.addAttribute("option", carEntryDraft); // draft 전체 or 필요한 속성 전달
        model.addAttribute("carId", carId);
        return "selldetail/sell_option"; // 파일명과 반드시 일치
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
        return "selldetail/car_confirm";
    }





}
