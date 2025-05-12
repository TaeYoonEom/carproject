package com.example.carproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form
                        .loginPage("/login")                 // 커스텀 로그인 페이지
                        .loginProcessingUrl("/login")        // 로그인 처리 경로
                        .defaultSuccessUrl("/", true)        // 로그인 성공 시 이동 경로
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")                // 로그아웃 경로
                        .logoutSuccessUrl("/")               // 로그아웃 후 리디렉션
                        .invalidateHttpSession(true)         // 세션 무효화
                        .deleteCookies("JSESSIONID")         // 쿠키 삭제
                        .permitAll()
                );

        return http.build();
    }
}
