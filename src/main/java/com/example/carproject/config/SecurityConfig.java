package com.example.carproject.config;

import com.example.carproject.security.CustomUserDetailsService;
import com.example.carproject.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ✅ 비밀번호 암호화용 Bean 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ 로그인 성공 후 원래 요청한 페이지로 리다이렉트 처리 핸들러
    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setTargetUrlParameter("redirect");      // 쿼리 파라미터 사용 가능: /login?redirect=...
        handler.setDefaultTargetUrl("/");               // 기본 리다이렉트 URL
        return handler;
    }

    // ✅ Spring Security 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomOAuth2UserService customOAuth2UserService,
                                           CustomUserDetailsService customUserDetailsService) throws Exception {

        http
                // ✅ 경로별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register", "/find-id", "/find-password", "/reset-password",
                                "/css/**", "/js/**", "/img/**", "/oauth2/**", "/error"
                        ).permitAll()
                        .requestMatchers("/mypage/**", "/sell/detail/**").authenticated()  // 로그인 필요 경로
                        .anyRequest().permitAll()
                )

                // ✅ CSRF 보안 설정 (개발 중엔 disable)
                .csrf(csrf -> csrf.disable())

                // ✅ 일반 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login")                                   // 로그인 페이지
                        .loginProcessingUrl("/login")                          // 로그인 폼 처리 URL
                        .successHandler(savedRequestAwareAuthenticationSuccessHandler())  // ✅ 원래 요청 복원
                        .permitAll()
                )

                // ✅ 사용자 인증 서비스 설정
                .userDetailsService(customUserDetailsService)

                // ✅ OAuth2 로그인 설정 (소셜 로그인)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")                                   // 소셜 로그인도 동일 페이지 사용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(savedRequestAwareAuthenticationSuccessHandler())  // ✅ 리다이렉션
                )

                // ✅ 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
