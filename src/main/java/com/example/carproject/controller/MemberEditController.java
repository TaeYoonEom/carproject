package com.example.carproject.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/mypage")
public class MemberEditController {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberEditController(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/verify")
    public String showVerifyForm(Model model, Authentication auth) {
        String loginId = auth.getName();
        model.addAttribute("loginId", loginId);
        return "verify";
    }

    @PostMapping("/verify")
    public String processVerify(@RequestParam String password, Authentication auth, Model model) {
        Member member = memberRepository.findByLoginId(auth.getName())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            model.addAttribute("loginId", auth.getName());
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "verify";
        }

        return "redirect:/mypage/info";
    }

    @GetMapping("/info")
    public String showInfoForm(Model model, Authentication auth) {
        Member member = memberRepository.findByLoginId(auth.getName())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
        model.addAttribute("member", member);
        return "info";
    }

    @PostMapping("/info/update")
    public String updateMember(@RequestParam String phone,
                               @RequestParam String address,
                               @RequestParam(required = false) String detailAddress, // 상세주소 추가
                               Authentication auth,
                               Model model) {

        Member member = memberRepository.findByLoginId(auth.getName())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        member.setPhone(phone);
        // ✅ 주소는 상세주소까지 합쳐서 저장
        member.setAddress(address + (detailAddress != null ? " " + detailAddress : ""));
        memberRepository.save(member);

        model.addAttribute("member", member);
        model.addAttribute("message", "회원정보가 수정되었습니다!");
        return "redirect:/mypage";
    }


    @PostMapping("/withdraw")
    public String processWithdrawal(
            @RequestParam String password,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String detail,
            Authentication auth,
            HttpSession session,
            Model model
    ) {
        String loginId = auth.getName();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("member", member);
            return "mypage"; // 또는 탈퇴 1단계 페이지로 돌아가기
        }

        // ✅ 회원 DB에서 삭제
        memberRepository.delete(member);

        // ✅ 세션 무효화 (자동 로그아웃)
        session.invalidate();

        // ✅ 로그로 탈퇴 사유 출력 (선택)
        System.out.println("탈퇴한 사용자: " + loginId + " / 사유: " + reason + " / 상세: " + detail);

        // ✅ 메인 페이지로 이동 또는 탈퇴 완료 페이지로 리다이렉트
        return "redirect:/";
    }
    @PostMapping("/withdraw-step1")
    public String goToWithdrawStep2(@RequestParam String password, Authentication auth, Model model) {
        Member member = memberRepository.findByLoginId(auth.getName())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            model.addAttribute("member", member);
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "mypage"; // 또는 탈퇴 1단계 템플릿 이름
        }

        // 🔐 비밀번호를 다음 단계로 넘겨주기 위해 model에 추가
        model.addAttribute("member", member);
        model.addAttribute("password", password);
        return "mypage"; // `withdraw-step2`가 포함된 same 템플릿
    }
}
