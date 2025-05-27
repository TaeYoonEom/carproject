package com.example.carproject.controller;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;


import java.util.Optional;
import java.util.Random;

@Controller
public class FindPasswordController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;  // ✅ 이 줄 꼭 필요함

    @GetMapping("/find-password")
    public String showFindPasswordPage() {
        return "find_password";
    }

    @PostMapping("/find-password")
    public String sendPasswordByEmail(@RequestParam String name,
                                      @RequestParam String username,
                                      @RequestParam String phone,
                                      Model model) {

        Optional<Member> memberOpt = memberRepository.findByNameAndLoginIdAndPhone(name, username, phone);

        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            String email = member.getEmail();

            // ✅ 임시 비밀번호 생성
            String tempPassword = generateTempPassword();

            // ✅ 암호화하여 DB에 저장
            member.setPassword(passwordEncoder.encode(tempPassword));
            memberRepository.save(member);

            model.addAttribute("username", username);  // 👈 이 줄 추가!!

            // ✅ 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Encar] 임시 비밀번호 안내");
            message.setText("회원님의 임시 비밀번호는 다음과 같습니다:\n\n" +
                    "임시 비밀번호: " + tempPassword + "\n\n" +
                    "로그인 후 반드시 비밀번호를 변경해 주세요.");

            try {
                mailSender.send(message);
                model.addAttribute("emailSent", true);
            } catch (Exception e) {
                e.printStackTrace();
                model.addAttribute("error", "이메일 전송에 실패했습니다: " + e.getMessage());
            }

        } else {
            model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
        }

        return "find_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username,
                                @RequestParam String tempPassword,
                                @RequestParam String newPassword,
                                Model model) {

        Optional<Member> memberOpt = memberRepository.findByLoginId(username);
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();

            // ✅ 임시 비밀번호 검증
            if (!passwordEncoder.matches(tempPassword, member.getPassword())) {
                model.addAttribute("username", username); // 다시 넘겨야 함
                model.addAttribute("error", "임시 비밀번호가 일치하지 않습니다.");
                return "reset_password";
            }

            // ✅ 새 비밀번호 저장
            member.setPassword(passwordEncoder.encode(newPassword));
            memberRepository.save(member);
            return "reset_success";
        } else {
            model.addAttribute("error", "사용자 정보를 찾을 수 없습니다.");
            return "reset_password";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "reset_password";
    }


    public String generateTempPassword() {
        int length = 10;
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randIndex = random.nextInt(charSet.length());
            sb.append(charSet.charAt(randIndex));
        }
        return sb.toString();
    }
}
