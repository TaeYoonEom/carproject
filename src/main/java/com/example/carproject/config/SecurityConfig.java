package com.example.carproject.config;

import com.example.carproject.security.CustomUserDetailsService;
import com.example.carproject.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ✅ 비밀번호 암호화용 Bean
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Security Filter Chain 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomOAuth2UserService customOAuth2UserService,
                                           CustomUserDetailsService customUserDetailsService) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register", "/find-id", "/find-password", "/reset-password",
                                "/css/**", "/js/**", "/img/**", "/oauth2/**", "/error"
                        ).permitAll()
                        .requestMatchers("/mypage/**").authenticated() // ✅ 마이페이지만 인증 필요
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화

                // ✅ 일반 form 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login")                // 사용자 지정 로그인 페이지
                        .loginProcessingUrl("/login")       // 로그인 form의 action
                        .defaultSuccessUrl("/", true)       // 로그인 성공 시 리다이렉트
                        .permitAll()
                )

                // ✅ UserDetailsService 설정
                .userDetailsService(customUserDetailsService)

                // ✅ 소셜 로그인 (OAuth2) 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/", true)
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
