package com.example.carproject.repository;

import com.example.carproject.domain.AccidentHistoryTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccidentHistoryTicketRepository
        extends JpaRepository<AccidentHistoryTicket, Long> {

    List<AccidentHistoryTicket> findByMember_MemberId(Integer memberId);
}