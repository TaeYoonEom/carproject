package com.example.carproject.service;

import com.example.carproject.domain.AccidentHistoryTicket;
import com.example.carproject.dto.AccidentHistoryTicketDto;
import com.example.carproject.repository.AccidentHistoryTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccidentHistoryTicketService {

    private final AccidentHistoryTicketRepository repo;

    private AccidentHistoryTicketDto toDto(AccidentHistoryTicket t) {

        long remain = 0;
        if (t.getExpireDate() != null) {
            remain = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    t.getExpireDate().toLocalDate()
            );
        }

        return AccidentHistoryTicketDto.builder()
                .id(t.getId())
                .purchaseDate(t.getPurchaseDate())
                .validDays(t.getValidDays())
                .remainingCount(t.getRemainingCount())
                .expireDate(t.getExpireDate())
                .status(t.getStatus())
                .remainDays(remain)
                .build();
    }

    public List<AccidentHistoryTicketDto> getTickets(Integer memberId) {
        return repo.findByMember_MemberId(memberId)
                .stream()
                .map(this::toDto)
                .toList();
    }
}

