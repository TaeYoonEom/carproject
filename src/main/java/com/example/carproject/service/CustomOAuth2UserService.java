package com.example.carproject.service;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(MemberRepository memberRepository,
                                   BCryptPasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // ✅ 카카오 응답 전체 로그 출력
        System.out.println("✅ [카카오 응답 정보]");
        attributes.forEach((key, value) -> System.out.println(" - " + key + ": " + value));

        // ✅ 카카오 계정 정보 파싱
        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        @SuppressWarnings("unchecked")
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        Long kakaoId = ((Number) attributes.get("id")).longValue(); // 고유한 카카오 ID

        // ✅ 고유 loginId 생성
        String loginId = "kakao_" + kakaoId;

        // ✅ DB에 loginId로 사용자 조회
        Optional<Member> userOptional = memberRepository.findByLoginId(loginId);
        Member member;

        if (userOptional.isEmpty()) {
            // 🔹 없다면 자동 회원가입 처리
            member = new Member();
            member.setLoginId(loginId);
            member.setName(nickname);
            member.setEmail("noemail+" + kakaoId + "@example.com"); // 더미 이메일
            member.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // 무작위 비밀번호
            member.setAddress("");
            member.setIsAddressPublic(false);

            memberRepository.save(member);
        } else {
            member = userOptional.get();
        }

        // ✅ OAuth2 인증 객체 반환
        Map<String, Object> customAttributes = new HashMap<>(attributes);
        customAttributes.put("id", kakaoId); // 식별자 명시

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                customAttributes,
                "id"
        );
    }
}
