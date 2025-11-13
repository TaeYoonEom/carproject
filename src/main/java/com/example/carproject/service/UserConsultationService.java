package com.example.carproject.service;

import com.example.carproject.domain.UserConsultation;
import com.example.carproject.repository.UserConsultationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConsultationService {

    private final UserConsultationRepository repository;

    public List<UserConsultation> getMyConsultations(Integer memberId) {
        return repository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public void save(Integer memberId, Integer id, String title, String content) {

        UserConsultation uc;

        // 🔥 수정 모드
        if (id != null) {
            uc = repository.findById(id)
                    .orElse(new UserConsultation()); // 혹은 예외 던져도 됨
        }
        // 🔥 신규 저장 모드
        else {
            uc = new UserConsultation();
            uc.setMemberId(memberId);
            uc.setCreatedAt(LocalDateTime.now());
        }

        uc.setTitle(title);
        uc.setContent(content);

        repository.save(uc);   // INSERT 또는 UPDATE 실행
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}

