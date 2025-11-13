package com.example.carproject.service;

import com.example.carproject.domain.Inquiry;
import com.example.carproject.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository repository;

    public List<Inquiry> getMyInquiries(Integer memberId) {
        return repository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public void save(Integer memberId, Integer carId, String message) {
        Inquiry inq = new Inquiry();
        inq.setMemberId(memberId);
        inq.setCarId(carId);
        inq.setMessage(message);
        inq.setCreatedAt(LocalDateTime.now());
        repository.save(inq);
    }
}
