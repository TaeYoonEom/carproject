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
import java.util.Optional;

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
    public String viewCarInfo(@RequestParam String carNumber, Principal principal, Model model) {
        String loginId = principal.getName();
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        Long memberId = member.getMemberId().longValue();

        CarEntryDraft draft = carEntryDraftRepository
                .findByMemberIdAndCarNumber(memberId, carNumber)
                .orElseThrow();

        model.addAttribute("car", draft);
        return "selldetail/car_view"; // templates/selldetail/car_view.html
    }


}
