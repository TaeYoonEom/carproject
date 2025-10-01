package com.example.carproject.config;

import com.example.carproject.domain.Member;
import com.example.carproject.repository.MemberRepository;
import com.example.carproject.service.WishlistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@ControllerAdvice
@Component
public class GlobalWishModelAdvice {

    private final MemberRepository memberRepository;
    private final WishlistService wishlistService;

    public GlobalWishModelAdvice(MemberRepository memberRepository,
                                 WishlistService wishlistService) {
        this.memberRepository = memberRepository;
        this.wishlistService = wishlistService;
    }

    @ModelAttribute("wishSet")
    public Set<Integer> wishSet(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return Collections.emptySet();
        }

        Object principal = authentication.getPrincipal();
        String loginId = null;

        if (principal instanceof UserDetails ud) {
            loginId = ud.getUsername();
        } else if (principal instanceof OAuth2User oauth) {
            Object idAttr = oauth.getAttribute("id");
            if (idAttr != null) {
                loginId = "kakao_" + String.valueOf(idAttr);
            }
        }

        if (loginId == null) return Collections.emptySet();

        Optional<Member> om = memberRepository.findByLoginId(loginId);
        if (om.isEmpty()) return Collections.emptySet();

        return wishlistService.myWishCarIds(om.get().getMemberId());
    }
}
